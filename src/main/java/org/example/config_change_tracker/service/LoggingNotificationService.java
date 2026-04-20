package org.example.config_change_tracker.service;

import org.example.config_change_tracker.model.ConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(LoggingNotificationService.class);

    @Override
    public void notifyCriticalChange(ConfigData data) {
        log.warn(
                "CRITICAL CONFIG CHANGE DETECTED: id={}, changeType={}, actionType={}, oldValue={}, newValue={}, timestamp={}",
                data.getId(),
                data.getChangeType(),
                data.getActionType(),
                data.getOldValue(),
                data.getNewValue(),
                data.getTimestamp()
        );
    }
}
