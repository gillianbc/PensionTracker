package com.gillianbc.pensionstracker.controller;

import com.gillianbc.pensionstracker.dto.PotDto;
import com.gillianbc.pensionstracker.dto.ProviderDto;
import com.gillianbc.pensionstracker.dto.SnapshotDto;
import com.gillianbc.pensionstracker.dto.TransactionDto;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ApiControllerTest extends AbstractControllerTest {

    @Test
    void providersCrudTest() throws Exception {
        // post
        ProviderDto saved = postProviderDto("TestProvider", "My notes");

        // get
        mockMvc.perform(get("/api/providers/" + saved.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestProvider"))
                .andDo(print())
        ;

        // get all
        mockMvc.perform(get("/api/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("TestProvider"))
                .andExpect(jsonPath("$[0].notes").value("My notes"));
    }

    @Test
    void potsCrudTest() throws Exception {
        // Create provider needed for pot
        ProviderDto savedProvider = postProviderDto("PotProvider", "Provider for test pot");

        // create pot
        PotDto savedPot = postPotDto(savedProvider);

        // get
        mockMvc.perform(get("/api/pots/" + savedPot.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedPot.id()))
                .andExpect(jsonPath("$.providerId").value(savedProvider.id()))
                .andExpect(jsonPath("$.name").value("MyPot"))
                .andExpect(jsonPath("$.currency").value("GBP"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.notes").value("Some notes"))
                .andExpect(jsonPath("$.planNumber").value("PN123"))
                .andExpect(jsonPath("$.schemeNumber").value("SN123"))
                .andDo(print())
        ;

        // get all
        mockMvc.perform(get("/api/pots"))
                .andExpect(status().isOk());
    }

    @Test
    void snapshotsCrudTest() throws Exception {
        // need a pot first
        ProviderDto savedProvider = postProviderDto("SnapProvider", "");

        PotDto savedPot = postPotDto(savedProvider);

        // create snapshot
        SnapshotDto savedSnap = postSnapshotDto(savedPot);

        // get
        mockMvc.perform(get("/api/snapshots/" + savedSnap.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedSnap.id()))
                .andExpect(jsonPath("$.potId").value(savedPot.id()))
                .andExpect(jsonPath("$.balance").value(123.45))
                .andExpect(jsonPath("$.source").value("USER"))
                .andExpect(jsonPath("$.note").value("Some note"))
                .andExpect(jsonPath("$.date").value(TEST_DATE.toString()))
                .andDo(print())
        ;
    }

    @Test
    void transactionCrudTest() throws Exception {
        // Create provider
        ProviderDto savedProvider = postProviderDto("Acme Finance", "");
        
        //Create pot
        PotDto savedPot = postPotDto(savedProvider);

        // Create transaction
        TransactionDto savedtransaction = postTransactionDto(savedPot);

        // get
        mockMvc.perform(get("/api/transactions/" + savedtransaction.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedtransaction.id()))
                .andExpect(jsonPath("$.potId").value(savedPot.id()))
                .andExpect(jsonPath("$.date").value(TEST_DATE.toString()))
                .andExpect(jsonPath("$.amount").value(77.25))
                .andExpect(jsonPath("$.note").value("Contribution"))
                .andDo(print());
    }

}
