package com.gillianbc.pensionstracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gillianbc.pensionstracker.dto.PotDto;
import com.gillianbc.pensionstracker.dto.ProviderDto;
import com.gillianbc.pensionstracker.dto.SnapshotDto;
import com.gillianbc.pensionstracker.dto.TransactionDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void providersCrudTest() throws Exception {
        // create
        ProviderDto provider = new ProviderDto(null, "TestProvider", "Notes");
        String providerJson = objectMapper.writeValueAsString(provider);

        String response = mockMvc.perform(post("/api/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(providerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        ProviderDto saved = objectMapper.readValue(response, ProviderDto.class);

        // get
        mockMvc.perform(get("/api/providers/" + saved.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestProvider"));

        // get all
        mockMvc.perform(get("/api/providers"))
                .andExpect(status().isOk());
    }

    @Test
    void potsCrudTest() throws Exception {
        // Create provider needed for pot
        ProviderDto provider = new ProviderDto(null, "PotProvider", "Provider for test pot");
        String providerJson = objectMapper.writeValueAsString(provider);
        String providerResponse = mockMvc.perform(post("/api/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(providerJson))
                .andReturn().getResponse().getContentAsString();
        ProviderDto savedProvider = objectMapper.readValue(providerResponse, ProviderDto.class);

        // create pot
        PotDto pot = new PotDto(null, savedProvider.id(), "MyPot", "GBP", "ACTIVE", "Some notes", "PN123", "SN123");
        String potJson = objectMapper.writeValueAsString(pot);

        String potResponse = mockMvc.perform(post("/api/pots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(potJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        PotDto savedPot = objectMapper.readValue(potResponse, PotDto.class);

        // get
        mockMvc.perform(get("/api/pots/" + savedPot.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("MyPot"));

        // get all
        mockMvc.perform(get("/api/pots"))
                .andExpect(status().isOk());
    }

    @Test
    void snapshotsCrudTest() throws Exception {
        // need a pot first
        ProviderDto provider = new ProviderDto(null, "SnapProvider", "");
        String providerJson = objectMapper.writeValueAsString(provider);
        String providerResponse = mockMvc.perform(post("/api/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(providerJson))
                .andReturn().getResponse().getContentAsString();
        ProviderDto savedProvider = objectMapper.readValue(providerResponse, ProviderDto.class);

        PotDto pot = new PotDto(null, savedProvider.id(), "SnapPot", "GBP", "ACTIVE", null, "123", null);
        String potJson = objectMapper.writeValueAsString(pot);
        String potResponse = mockMvc.perform(post("/api/pots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(potJson))
                .andReturn().getResponse().getContentAsString();
        PotDto savedPot = objectMapper.readValue(potResponse, PotDto.class);

        // create snapshot
        SnapshotDto snapshot = new SnapshotDto(null, savedPot.id(), LocalDate.now(), Double.valueOf(123.45), "USER", "Some note");
        String snapJson = objectMapper.writeValueAsString(snapshot);

        String snapResp = mockMvc.perform(post("/api/snapshots")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(snapJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();
        SnapshotDto savedSnap = objectMapper.readValue(snapResp, SnapshotDto.class);

        // get
        mockMvc.perform(get("/api/snapshots/" + savedSnap.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(123.45));
    }

    @Test
    void transactionCrudTest() throws Exception {
        // Create provider & pot
        ProviderDto provider = new ProviderDto(null, "TxProvider", "");
        String providerJson = objectMapper.writeValueAsString(provider);
        String providerResponse = mockMvc.perform(post("/api/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(providerJson))
                .andReturn().getResponse().getContentAsString();
        ProviderDto savedProvider = objectMapper.readValue(providerResponse, ProviderDto.class);

        PotDto pot = new PotDto(null, savedProvider.id(), "TxPot", "GBP", "ACTIVE", "", null, null);
        String potJson = objectMapper.writeValueAsString(pot);
        String potResponse = mockMvc.perform(post("/api/pots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(potJson))
                .andReturn().getResponse().getContentAsString();
        PotDto savedPot = objectMapper.readValue(potResponse, PotDto.class);

        // Create tx
        TransactionDto tx = new TransactionDto(null, savedPot.id(), LocalDate.now(), "IN", Double.valueOf(77.25), "Contrib");
        String txJson = objectMapper.writeValueAsString(tx);

        String txResp = mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(txJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();
        TransactionDto savedTx = objectMapper.readValue(txResp, TransactionDto.class);

        // get
        mockMvc.perform(get("/api/transactions/" + savedTx.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(77.25));
    }
}
