package org.example.config_change_tracker.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

public class ConfigData {
    private UUID id;
    private String accountId; // IBAN
    private ChangeType changeType;
    private ActionType actionType; //add, update, delete
    private ApprovalType approvalType;
    private JsonNode oldValue;
    private JsonNode newValue;
    private Instant timestamp;
    private boolean isCritical;

    public ConfigData(UUID id, ChangeType changeType, ActionType actionType, ApprovalType approvalType, JsonNode oldValue, JsonNode newValue, Instant timestamp, boolean isCritical) {
        this.id = id;
        this.changeType = changeType;
        this.actionType = actionType;
        this.approvalType = approvalType;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.timestamp = timestamp;
        this.isCritical = isCritical;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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
        return isCritical;
    }

    public void setDeleted(boolean isCritical) {
        this.isCritical = isCritical;
    }

    public ApprovalType getApprovalType() {
        return approvalType;
    }

    public void setApprovalType(ApprovalType approvalType) {
        this.approvalType = approvalType;
    }
}
