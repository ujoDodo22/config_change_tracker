package org.example.config_change_tracker.service;

import org.example.config_change_tracker.model.ConfigData;

public interface NotificationService {
    void notifyCriticalChange(ConfigData data);
}
