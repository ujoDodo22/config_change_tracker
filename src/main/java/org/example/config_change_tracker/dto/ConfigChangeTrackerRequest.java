package org.example.config_change_tracker.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import org.example.config_change_tracker.model.ActionType;
import org.example.config_change_tracker.model.ChangeType;

public class ConfigChangeTrackerRequest {

    @NotNull(message = "ChangeType is required")
    private ChangeType changeType;

    @NotNull(message = "ChangeType is required")
    private ActionType actionType;

    private JsonNode newValue;
    private JsonNode oldValue;

    private boolean isCritical;

    public ConfigChangeTrackerRequest() {}

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public JsonNode getNewValue() {
        return newValue;
    }

    public void setNewValue(JsonNode newValue) {
        this.newValue = newValue;
    }

    public JsonNode getOldValue() {
        return oldValue;
    }

    public void setOldValue(JsonNode oldValue) {
        this.oldValue = oldValue;
    }
}
