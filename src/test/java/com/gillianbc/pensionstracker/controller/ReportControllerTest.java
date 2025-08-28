package com.gillianbc.pensionstracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gillianbc.pensionstracker.dto.PotDto;
import com.gillianbc.pensionstracker.dto.ProviderDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        PotDto pot = new PotDto(null, savedProvider.id(), "ReportPot", "GBP", "ACTIVE", "", "", "");
        String potJson = objectMapper.writeValueAsString(pot);
        String potResponse = mockMvc.perform(post("/api/pots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(potJson))
                .andReturn().getResponse().getContentAsString();
        PotDto savedPot = objectMapper.readValue(potResponse, PotDto.class);

        mockMvc.perform(get("/api/reports/" + savedPot.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.potId").value(savedPot.id()));
    }

    @Test
    void getReport_notFound_returns5xxOr4xx() throws Exception {
        mockMvc.perform(get("/api/reports/9999999"))
                .andExpect(status().is4xxClientError());
    }
}
