package com.gillianbc.pensionstracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gillianbc.pensionstracker.dto.PotDto;
import com.gillianbc.pensionstracker.dto.ProviderDto;
import com.gillianbc.pensionstracker.dto.SnapshotDto;
import com.gillianbc.pensionstracker.dto.TransactionDto;
import com.gillianbc.pensionstracker.repo.PotRepo;
import com.gillianbc.pensionstracker.repo.ProviderRepo;
import com.gillianbc.pensionstracker.repo.SnapshotRepo;
import com.gillianbc.pensionstracker.repo.TransactionRepo;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AbstractControllerTest {

    public static final LocalDate TEST_DATE = LocalDate.of(2023, 1, 1);

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ProviderRepo providerRepo;
    @Autowired
    protected TransactionRepo transactionRepo;
    @Autowired
    protected SnapshotRepo snapshotRepo;
    @Autowired
    protected PotRepo potRepo;

    @BeforeEach
    void tearDown() {
        snapshotRepo.deleteAll();
        transactionRepo.deleteAll();
        potRepo.deleteAll();
        providerRepo.deleteAll();
    }

    public ProviderDto postProviderDto(String providerName, String notes) throws Exception {
        ProviderDto provider = new ProviderDto(null, providerName, notes);
        String providerJson = objectMapper.writeValueAsString(provider);
        String providerResponse = mockMvc.perform(post("/api/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(providerJson))
                .andReturn().getResponse().getContentAsString();
        ProviderDto savedProvider = objectMapper.readValue(providerResponse, ProviderDto.class);
        return savedProvider;
    }

    public PotDto postPotDto(ProviderDto provider) throws Exception {
        PotDto pot = new PotDto(null, provider.id(), "MyPot",
                "GBP", "ACTIVE", "Some notes", "PN123", "SN123");
        String potJson = objectMapper.writeValueAsString(pot);

        String potResponse = mockMvc.perform(post("/api/pots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(potJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        PotDto savedPot = objectMapper.readValue(potResponse, PotDto.class);
        return savedPot;
    }

    public SnapshotDto postSnapshotDto(PotDto pensionPot) throws Exception {
        return postSnapshotDto(pensionPot, 123.45, TEST_DATE);
    }

    public SnapshotDto postSnapshotDto(PotDto pensionPot, double amount, LocalDate date) throws Exception {
        SnapshotDto snapshot = new SnapshotDto(null, pensionPot.id(), date, Double.valueOf(amount), "USER", "Some note");
        String snapJson = objectMapper.writeValueAsString(snapshot);

        String snapResp = mockMvc.perform(post("/api/snapshots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(snapJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();
        SnapshotDto savedSnap = objectMapper.readValue(snapResp, SnapshotDto.class);
        return savedSnap;
    }

    public TransactionDto postTransactionDto(PotDto pot) throws Exception {
        TransactionDto transaction = new TransactionDto(null, pot.id(),
                TEST_DATE, "IN", Double.valueOf(77.25), "Contribution");
        String transactionJson = objectMapper.writeValueAsString(transaction);

        String transactionResp = mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();
        TransactionDto savedtransaction = objectMapper.readValue(transactionResp, TransactionDto.class);
        return savedtransaction;
    }
}
