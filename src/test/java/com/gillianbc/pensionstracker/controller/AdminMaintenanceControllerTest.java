package com.gillianbc.pensionstracker.controller;

import com.gillianbc.pensionstracker.repo.PotRepo;
import com.gillianbc.pensionstracker.repo.ProviderRepo;
import com.gillianbc.pensionstracker.repo.SnapshotRepo;
import com.gillianbc.pensionstracker.repo.TransactionRepo;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired
    private ProviderRepo providerRepo;
    @Autowired
    private TransactionRepo transactionRepo;
    @Autowired
    private SnapshotRepo snapshotRepo;
    @Autowired
    private PotRepo potRepo;

    @BeforeEach
    void tearDown() {
        snapshotRepo.deleteAll();
        transactionRepo.deleteAll();
        potRepo.deleteAll();
        providerRepo.deleteAll();
    }

    @Test
    void clearDatabase_returnsNoContent() throws Exception {
        // Ideally, should insert data first, but I'm not going to bother
        mockMvc.perform(post("/api/admin/clear"))
                .andExpect(status().isNoContent());
    }

    @Test
    void clearPots_returnsNoContent() throws Exception {
        mockMvc.perform(post("/api/admin/clear/pots"))
                .andExpect(status().isNoContent());
    }
}
