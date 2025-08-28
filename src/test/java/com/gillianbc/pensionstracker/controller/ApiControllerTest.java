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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiControllerTest {

    public static final LocalDate TEST_DATE = LocalDate.of(2023, 1, 1);

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

    public TransactionDto postTransactionDto(PotDto savedPot) throws Exception {
        TransactionDto transaction = new TransactionDto(null, savedPot.id(),
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

    public PotDto postPotDto(ProviderDto savedProvider) throws Exception {
        PotDto pot = new PotDto(null, savedProvider.id(), "MyPot",
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

    public SnapshotDto postSnapshotDto(PotDto savedPot) throws Exception {
        SnapshotDto snapshot = new SnapshotDto(null, savedPot.id(), TEST_DATE, Double.valueOf(123.45), "USER", "Some note");
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
}
