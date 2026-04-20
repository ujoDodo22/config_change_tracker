package org.example.config_change_tracker.dto;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.config_change_tracker.model.ActionType;
import org.example.config_change_tracker.model.ChangeType;

import java.time.Instant;
import java.util.UUID;

public class ConfigChangeTrackerResponse {

    private UUID id;
    private ChangeType type;
    private ActionType action;
    private JsonNode oldValue;
    private JsonNode newValue;
    private Instant timestamp;
    private boolean critical;

    public ConfigChangeTrackerResponse(UUID id, ChangeType type, ActionType action, JsonNode oldValue, JsonNode newValue, Instant timestamp, boolean critical) {
        this.id = id;
        this.type = type;
        this.action = action;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.timestamp = timestamp;
        this.critical = critical;
    }


    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public ChangeType getType() {
        return type;
    }

    public void setType(ChangeType type) {
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public JsonNode getOldValue() {
        return oldValue;
    }

    public void setOldValue(JsonNode oldValue) {
        this.oldValue = oldValue;
    }

    public JsonNode getNewValue() {
        return newValue;
    }

    public void setNewValue(JsonNode newValue) {
        this.newValue = newValue;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isCritical() {
        return critical;
    }

    public void setCritical(boolean critical) {
        this.critical = critical;
    }
}
