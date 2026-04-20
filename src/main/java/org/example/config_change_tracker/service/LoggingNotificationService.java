package org.example.config_change_tracker.service;

import org.example.config_change_tracker.exception.ExternalNotificationException;
import org.example.config_change_tracker.model.ConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LoggingNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(LoggingNotificationService.class);

    @Override
    @Retryable(
            retryFor = ExternalNotificationException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 500)
    )
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

/* comment out for simulating failure
    private final AtomicInteger attempts = new AtomicInteger(0);

    @Override
    @Retryable(
            retryFor = ExternalNotificationException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 300)
    )
    public void notifyCriticalChange(ConfigData data) {
        int currentAttempt = attempts.incrementAndGet();
        log.warn("Sending critical change notification for id={}, attempt={}", data.getId(), currentAttempt);

        if (currentAttempt < 3) {
            throw new ExternalNotificationException("Simulated external monitoring service failure");
        }

        log.info("Notification sent successfully for id={}", data.getId());
        attempts.set(0);
    }
    */
    @Recover
    public void recover(ExternalNotificationException ex, ConfigData data) {
        log.error("Notification failed after retries for id={}. Reason: {}", data.getId(), ex.getMessage());
    }
}
