package com.gillianbc.pensionstracker.controller;

import com.gillianbc.pensionstracker.dto.PotDto;
import com.gillianbc.pensionstracker.dto.ProviderDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReportControllerTest extends AbstractControllerTest {

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
        ProviderDto provider = postProviderDto("TestProvider", "My notes");

        PotDto pot = postPotDto(provider);

        // create snapshot for start
        postSnapshotDto(pot, 100.00, TEST_DATE);
        postSnapshotDto(pot, 125.00, TEST_DATE.plusYears(1));

        
        mockMvc.perform(get("/api/reports/" + pot.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.potId").value(pot.id()))
                .andExpect(jsonPath("$.fromDate").value("2023-01-01"))
                .andExpect(jsonPath("$.toDate").value("2024-01-01"))
                .andExpect(jsonPath("$.openingBalance").value(100.00))
                .andExpect(jsonPath("$.currentBalance").value(125.00))
                .andExpect(jsonPath("$.contributionsExclRebates").value(0.0))
                .andExpect(jsonPath("$.contributionsInclRebates").value(0.0))
                .andExpect(jsonPath("$.netFlows").value(0.0))
                .andExpect(jsonPath("$.growth").value(25.0))
                .andExpect(jsonPath("$.cagrAnnualPercent").value((Object) 0.25))
                .andDo(print());
        
    }

    @Test
    void getReport_notFound_returns5xxOr4xx() throws Exception {
        mockMvc.perform(get("/api/reports/9999999"))
                .andExpect(status().is4xxClientError());
    }
}
