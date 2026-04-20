package org.example.config_change_tracker.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ConfigChangeTrackerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateConfigChange() throws Exception {
        String body = """
                {
                  "changeType": "CREDIT_LIMIT",
                  "actionType": "ADD",
                  "newValue": 5000
                }
                """;

        mockMvc.perform(post("/api/config-changes")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.type").value("CREDIT_LIMIT"));
    }

    @Test
    void shouldFailInvalidEnum() throws Exception {
        String body = """
                {
                  "changeType": "INVALID",
                  "actionType": "ADD",
                  "newValue": 5000
                }
                """;

        mockMvc.perform(post("/api/config-changes")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAll() throws Exception {
        mockMvc.perform(get("/api/config-changes"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFailInvalidUUID() throws Exception {
        mockMvc.perform(get("/api/config-changes/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("UUID")));
    }
}