package org.example.config_change_tracker.repository;

import org.example.config_change_tracker.model.ChangeType;
import org.example.config_change_tracker.model.ConfigData;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ConfigChangeTrackerDb {

    private final Hashtable<UUID, ConfigData> database = new Hashtable<>();

    public ConfigData save(ConfigData data) {
        database.put(data.getId(), data);
        return data;
    }

    public ConfigData getById (UUID id)
    {
        return database.get(id);
    }

    public List<ConfigData> getAll() {
        return new ArrayList<>(database.values());
    }
}
