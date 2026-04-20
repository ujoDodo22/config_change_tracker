package org.example.config_change_tracker.service;

import jakarta.validation.Valid;
import org.example.config_change_tracker.dto.ConfigChangeTrackerRequest;
import org.example.config_change_tracker.dto.ConfigChangeTrackerResponse;
import org.example.config_change_tracker.exception.ItemNotFoundException;
import org.example.config_change_tracker.model.ActionType;
import org.example.config_change_tracker.model.ApprovalType;
import org.example.config_change_tracker.model.ChangeType;
import org.example.config_change_tracker.model.ConfigData;
import org.example.config_change_tracker.repository.ConfigChangeTrackerDb;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

// metrics
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

// correlation id
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ConfigChangeTrackerService {

    private static final String ERROR_INVALID_PARAMETER_CREDIT_LIMIT = "for %s type of change, %s must be a number greater then 0";
    private static final String ERROR_INVALID_PARAMETER_APPROVAL_POLICY = "for %s type of change, %s must be a string. Possible values are AUTO or MANUAL";

    private final ConfigChangeTrackerDb repository;
    private final NotificationService notificationService;

    // metrics
    private final Counter totalChangesCounter;
    private final Counter criticalChangesCounter;

    // correlation id
    private static final Logger log = LoggerFactory.getLogger(ConfigChangeTrackerService.class);

    public ConfigChangeTrackerService(ConfigChangeTrackerDb repository, NotificationService notificationService, MeterRegistry meterRegistry) {
        this.repository = repository;
        this.notificationService = notificationService;

        this.totalChangesCounter = meterRegistry.counter("config.changes.total");
        this.criticalChangesCounter = meterRegistry.counter("config.changes.critical");
    }

    @PostMapping
    public ConfigChangeTrackerResponse create(@Valid @RequestBody ConfigChangeTrackerRequest request) {
        validateRequest(request);

        ConfigData change = new ConfigData(
                UUID.randomUUID(),
                request.getChangeType(),
                request.getActionType(),
                GetApprovalType(request),
                request.getOldValue(),
                request.getNewValue(),
                Instant.now(),
                IsCritical(request)
        );

        repository.save(change);

        log.info("Creating config change: changeType={}, actionType={}",
                request.getChangeType(),
                request.getActionType());

        totalChangesCounter.increment();

        if (change.isCritical()) {
            criticalChangesCounter.increment();
            notificationService.notifyCriticalChange(change);
        }

        return makeResponseFromRequest(change);
    }

    public List<ConfigChangeTrackerResponse> getWhere(ChangeType changeType, Instant from, Instant to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("'from' must be before or equal to 'to'");
        }

        return repository.getAll().stream()
                .filter(data -> changeType == null || data.getChangeType() == changeType)
                .filter(data -> from == null || !data.getTimestamp().isBefore(from))
                .filter(data -> to == null || !data.getTimestamp().isAfter(to))
                .map(this::makeResponseFromRequest)
                .toList();
    }

    public ConfigChangeTrackerResponse getById(UUID id)
    {
        ConfigData data = repository.getById(id);
        if(data == null)
        {
            throw new ItemNotFoundException("Config change with ID " + id + " does not exist");
        }
        return makeResponseFromRequest(data);
    }


    private void validateRequest(ConfigChangeTrackerRequest request) {
        ActionTypeValidation(request);
        ChangeTypeValidation(request);
    }

    private ConfigChangeTrackerResponse makeResponseFromRequest(ConfigData change) {
        return new ConfigChangeTrackerResponse(
                change.getId(),
                change.getChangeType(),
                change.getActionType(),
                change.getOldValue(),
                change.getNewValue(),
                change.getTimestamp(),
                change.isCritical()
        );
    }

    private ApprovalType GetApprovalType(ConfigChangeTrackerRequest request) {
        return ApprovalType.AUTO_APPROVE;
    }

    private boolean IsCritical(ConfigChangeTrackerRequest request) {
        if(request.getChangeType()==ChangeType.CREDIT_LIMIT)
        {
            if ( (request.getNewValue() != null) && (request.getOldValue() != null) &&
                    (request.getNewValue().asInt() - request.getOldValue().asInt()) > 3000 )
            {
                return true;
            }
        }
        else if (request.getChangeType()==ChangeType.APPROVAL_POLICY) {
            if( (request.getOldValue() != null) && request.getOldValue().asText().equals("MANUAL") && request.getNewValue().asText().equals("AUTO"))
                return true;
        }
        return false;
    }

    private void ActionTypeValidation(ConfigChangeTrackerRequest request)
    {
        if ((request.getActionType() == ActionType.ADD) && (request.getNewValue() == null))
        {
            throw new IllegalArgumentException("newValue field cannot be empty when actionType equals ADD.");
        }
        if ((request.getActionType() == ActionType.UPDATE) && ((request.getNewValue() == null) || (request.getOldValue() == null)))
        {
            throw new IllegalArgumentException("newValue and oldValue fields cannot be empty when actionType equals UPDATE.");
        }
        if ((request.getActionType() == ActionType.DELETE) && (request.getOldValue() == null))
        {
            throw new IllegalArgumentException("oldValue field cannot be empty when actionType equals DELETE.");
        }

        if ((request.getActionType() == ActionType.ADD) && (request.getOldValue() != null))
            throw new IllegalArgumentException("oldValue field is forbidden when actionType equals ADD");

        if ((request.getActionType() == ActionType.UPDATE) &&
                (request.getNewValue() != null) && (request.getOldValue() != null) &&
                request.getOldValue().equals(request.getNewValue()))
            throw new IllegalArgumentException("oldValue cannot be equal to newValue.");

        if ((request.getActionType() == ActionType.DELETE) && (request.getNewValue() != null))
            throw new IllegalArgumentException("newValue field is forbidden when actionType equals DELETE");
    }

    private void ChangeTypeValidation(ConfigChangeTrackerRequest request) {
        if ((request.getChangeType() != ChangeType.CREDIT_LIMIT) && (request.getChangeType() != ChangeType.APPROVAL_POLICY))
        {
            throw new IllegalArgumentException("changeType has to be CREDIT_LIMIT or APPROVAL_POLICY");
        }

        if (request.getChangeType() == ChangeType.CREDIT_LIMIT) {

            if (request.getNewValue() != null && (!request.getNewValue().isNumber() || (request.getNewValue().asInt() < 0))) {
                throw new IllegalArgumentException(ERROR_INVALID_PARAMETER_CREDIT_LIMIT.formatted("CREDIT_LIMIT", "newValue"));
            }

            if (request.getOldValue() != null && (!request.getOldValue().isNumber() || (request.getOldValue().asInt() < 0))) {
                throw new IllegalArgumentException(ERROR_INVALID_PARAMETER_CREDIT_LIMIT.formatted("CREDIT_LIMIT", "oldValue"));
            }
        }
        else if (request.getChangeType() == ChangeType.APPROVAL_POLICY) {
            if (request.getNewValue() != null && !request.getNewValue().isTextual()) {
                throw new IllegalArgumentException(ERROR_INVALID_PARAMETER_APPROVAL_POLICY .formatted("APPROVAL_POLICY", "newValue"));
            }

            if (request.getOldValue() != null && !request.getOldValue().isTextual()) {
                throw new IllegalArgumentException(ERROR_INVALID_PARAMETER_APPROVAL_POLICY .formatted("APPROVAL_POLICY", "oldValue"));
            }
        }
    }
}
