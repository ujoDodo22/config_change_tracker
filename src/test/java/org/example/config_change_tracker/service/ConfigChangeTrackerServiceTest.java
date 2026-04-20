package org.example.config_change_tracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config_change_tracker.dto.ConfigChangeTrackerRequest;
import org.example.config_change_tracker.model.ActionType;
import org.example.config_change_tracker.model.ChangeType;
import org.example.config_change_tracker.model.ConfigData;
import org.example.config_change_tracker.repository.ConfigChangeTrackerDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfigChangeTrackerServiceTest {

    @Mock
    private ConfigChangeTrackerDb repository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ConfigChangeTrackerService service;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldCreateCreditLimitAdd() {
        ConfigChangeTrackerRequest request = new ConfigChangeTrackerRequest();
        request.setChangeType(ChangeType.CREDIT_LIMIT);
        request.setActionType(ActionType.ADD);
        request.setNewValue(objectMapper.valueToTree(5000));

        when(repository.save(any(ConfigData.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.create(request);

        assertNotNull(response);
        assertEquals(ChangeType.CREDIT_LIMIT, response.getType());
        assertEquals(ActionType.ADD, response.getAction());

        verify(repository, times(1)).save(any());
    }

    @Test
    void shouldThrowWhenDeleteWithoutOldValue() {
        ConfigChangeTrackerRequest request = new ConfigChangeTrackerRequest();
        request.setChangeType(ChangeType.CREDIT_LIMIT);
        request.setActionType(ActionType.DELETE);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(request)
        );

        assertTrue(ex.getMessage().contains("oldValue"));
        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCreditLimitNotNumber() {
        ConfigChangeTrackerRequest request = new ConfigChangeTrackerRequest();
        request.setChangeType(ChangeType.CREDIT_LIMIT);
        request.setActionType(ActionType.ADD);
        request.setNewValue(objectMapper.valueToTree("abc"));

        assertThrows(IllegalArgumentException.class,
                () -> service.create(request));
    }

    @Test
    void shouldNotifyWhenCriticalChange() {
        ConfigChangeTrackerRequest request = new ConfigChangeTrackerRequest();
        request.setChangeType(ChangeType.CREDIT_LIMIT);
        request.setActionType(ActionType.UPDATE);
        request.setOldValue(objectMapper.valueToTree(1000));
        request.setNewValue(objectMapper.valueToTree(6000)); // rozdiel > 3000

        when(repository.save(any(ConfigData.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.create(request);

        verify(notificationService, times(1))
                .notifyCriticalChange(any(ConfigData.class));
    }

    @Test
    void shouldNotNotifyWhenNotCritical() {
        ConfigChangeTrackerRequest request = new ConfigChangeTrackerRequest();
        request.setChangeType(ChangeType.CREDIT_LIMIT);
        request.setActionType(ActionType.UPDATE);
        request.setOldValue(objectMapper.valueToTree(1000));
        request.setNewValue(objectMapper.valueToTree(2000)); // rozdiel < 3000

        when(repository.save(any(ConfigData.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.create(request);

        verify(notificationService, never())
                .notifyCriticalChange(any());
    }
}