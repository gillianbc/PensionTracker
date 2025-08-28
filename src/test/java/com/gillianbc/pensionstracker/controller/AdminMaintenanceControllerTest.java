package com.gillianbc.pensionstracker.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminMaintenanceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void clearDatabase_returnsNoContent() throws Exception {
        mockMvc.perform(post("/api/admin/clear"))
                .andExpect(status().isNoContent());
    }

    @Test
    void clearPots_returnsNoContent() throws Exception {
        mockMvc.perform(post("/api/admin/clear/pots"))
                .andExpect(status().isNoContent());
    }
}
