package com.gillianbc.pensionstracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gillianbc.pensionstracker.dto.PotDto;
import com.gillianbc.pensionstracker.dto.ProviderDto;
import com.gillianbc.pensionstracker.dto.SnapshotDto;
import com.gillianbc.pensionstracker.repo.PotRepo;
import com.gillianbc.pensionstracker.repo.ProviderRepo;
import com.gillianbc.pensionstracker.repo.SnapshotRepo;
import com.gillianbc.pensionstracker.repo.TransactionRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.gillianbc.pensionstracker.controller.ApiControllerTest.TEST_DATE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    void getReport_returnsReportDto() throws Exception {
        // Setup pot and provider
        ProviderDto provider = new ProviderDto(null, "ReportProv", "");
        String providerJson = objectMapper.writeValueAsString(provider);
        String providerResponse = mockMvc.perform(post("/api/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(providerJson))
                .andReturn().getResponse().getContentAsString();
        ProviderDto savedProvider = objectMapper.readValue(providerResponse, ProviderDto.class);

        PotDto pot = new PotDto(null, savedProvider.id(), "ReportPot",
                "GBP", "ACTIVE", "", "", "");
        String potJson = objectMapper.writeValueAsString(pot);
        String potResponse = mockMvc.perform(post("/api/pots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(potJson))
                .andReturn().getResponse().getContentAsString();
        PotDto savedPot = objectMapper.readValue(potResponse, PotDto.class);

        // create snapshot
        SnapshotDto snapshot = new SnapshotDto(null, savedPot.id(), TEST_DATE, Double.valueOf(123.45), "USER", "Some note");
        String snapJson = objectMapper.writeValueAsString(snapshot);



        mockMvc.perform(get("/api/reports/" + savedPot.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.potId").value(savedPot.id()))
                .andExpect(jsonPath("$.fromDate").value(null))
                .andExpect(jsonPath("$.toDate").value(null))
                .andDo(print());
        // {"potId":902,"fromDate":null,"toDate":null,"openingBalance":0.0,"currentBalance":0.0,
        // "contributionsExclRebates":0.0,
        // "contributionsInclRebates":0.0,"netFlows":0.0,"growth":0.0,"irrAnnualPercent":null}
    }

    @Test
    void getReport_notFound_returns5xxOr4xx() throws Exception {
        mockMvc.perform(get("/api/reports/9999999"))
                .andExpect(status().is4xxClientError());
    }
}
