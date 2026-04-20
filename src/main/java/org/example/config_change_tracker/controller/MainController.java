package org.example.config_change_tracker.controller;

import jakarta.validation.Valid;
import org.example.config_change_tracker.dto.ConfigChangeTrackerRequest;
import org.example.config_change_tracker.dto.ConfigChangeTrackerResponse;
import org.example.config_change_tracker.model.ChangeType;
import org.example.config_change_tracker.service.ConfigChangeTrackerService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/config-changes")
public class MainController {

    private final ConfigChangeTrackerService service;

    public MainController(ConfigChangeTrackerService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConfigChangeTrackerResponse create(@Valid @RequestBody ConfigChangeTrackerRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<ConfigChangeTrackerResponse> getWhere(
            @RequestParam(required = false) ChangeType type,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
    ) {
        return service.getWhere(type, from, to);
    }

    @GetMapping("/{id}")
    public ConfigChangeTrackerResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

}
