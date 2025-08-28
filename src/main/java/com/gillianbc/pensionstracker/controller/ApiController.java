package com.gillianbc.pensionstracker.controller;

import com.gillianbc.pensionstracker.dto.PotDto;
import com.gillianbc.pensionstracker.dto.ProviderDto;
import com.gillianbc.pensionstracker.dto.SnapshotDto;
import com.gillianbc.pensionstracker.dto.TransactionDto;
import com.gillianbc.pensionstracker.model.Pot;
import com.gillianbc.pensionstracker.model.Provider;
import com.gillianbc.pensionstracker.model.Snapshot;
import com.gillianbc.pensionstracker.model.Transaction;
import com.gillianbc.pensionstracker.repo.PotRepo;
import com.gillianbc.pensionstracker.repo.ProviderRepo;
import com.gillianbc.pensionstracker.repo.SnapshotRepo;
import com.gillianbc.pensionstracker.repo.TransactionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
class ApiController {
    private final ProviderRepo providers;
    private final PotRepo pots;
    private final SnapshotRepo snaps;
    private final TransactionRepo txs;

    // Providers

    @GetMapping("/providers")
    List<ProviderDto> listProviders() {
        return providers.findAll().stream()
                .map(ApiController::toProviderDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/providers/{id}")
    ProviderDto getProvider(@PathVariable Long id) {
        return providers.findById(id)
                .map(ApiController::toProviderDto)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Provider not found"));
    }

    @PostMapping("/providers")
    ProviderDto addProvider(@RequestBody ProviderDto p) {
        Provider saved = providers.save(fromProviderDto(p));
        return toProviderDto(saved);
    }

    // Pots

    @GetMapping("/pots")
    List<PotDto> listPots() {
        return pots.findAll().stream()
                .map(pot -> toPotDto(pot, true))
                .collect(Collectors.toList());
    }

    @GetMapping("/pots/{id}")
    PotDto getPot(@PathVariable Long id) {
        return pots.findById(id)
                .map(p -> toPotDto(p, true))
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Pot not found"));
    }

    @PostMapping("/pots")
    PotDto addPot(@RequestBody PotDto p) {
        Pot pot = fromPotDto(p, providers);
        Pot saved = pots.save(pot);
        // return with provider info
        return toPotDto(saved, true);
    }

    // Snapshots

    @GetMapping("/snapshots/{id}")
    SnapshotDto getSnapshot(@PathVariable Long id) {
        return snaps.findById(id)
                .map(ApiController::toSnapshotDto)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Snapshot not found"));
    }

    @PostMapping("/snapshots")
    SnapshotDto addSnap(@RequestBody SnapshotDto s) {
        Snapshot snap = fromSnapshotDto(s, pots);
        Snapshot saved = snaps.save(snap);
        return toSnapshotDto(saved);
    }

    // Transactions

    @GetMapping("/transactions/{id}")
    TransactionDto getTransaction(@PathVariable Long id) {
        return txs.findById(id)
                .map(ApiController::toTransactionDto)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Transaction not found"));
    }

    @PostMapping("/transactions")
    TransactionDto addTx(@RequestBody TransactionDto t) {
        Transaction tx = fromTransactionDto(t, pots);
        Transaction saved = txs.save(tx);
        return toTransactionDto(saved);
    }

    // Entity -> DTO Mappers

    private static ProviderDto toProviderDto(Provider p) {
        if (p == null) return null;
        return new ProviderDto(p.getId(), p.getName(), p.getNotes());
    }

    private static PotDto toPotDto(Pot pot, boolean includeProvider) {
        if (pot == null) return null;
        return new PotDto(
                pot.getId(),
                pot.getProvider() != null ? pot.getProvider().getId() : null,
                pot.getName(),
                pot.getCurrency(),
                pot.getStatus(),
                pot.getNotes(),
                pot.getPlanNumber(),
                pot.getSchemeNumber() // may be null
        );
    }

    private static SnapshotDto toSnapshotDto(Snapshot s) {
        if (s == null) return null;
        return new SnapshotDto(
                s.getId(),
                s.getPot() == null ? null : s.getPot().getId(),
                s.getDate(),
                s.getBalance(),
                s.getSource(),
                s.getNote()
        );
    }

    private static TransactionDto toTransactionDto(Transaction t) {
        if (t == null) return null;
        return new TransactionDto(
                t.getId(),
                t.getPot() == null ? null : t.getPot().getId(),
                t.getDate(),
                t.getType(),
                t.getAmount(),
                t.getNote()
        );
    }

    // DTO -> Entity Mappers

    private static Provider fromProviderDto(ProviderDto dto) {
        Provider provider = new Provider();
        provider.setId(dto.id());
        provider.setName(dto.name());
        provider.setNotes(dto.notes());
        return provider;
    }

    private static Pot fromPotDto(PotDto dto, ProviderRepo providerRepo) {
        Pot pot = new Pot();
        pot.setId(dto.id());
        pot.setName(dto.name());
        pot.setCurrency(dto.currency());
        pot.setStatus(dto.status());
        pot.setNotes(dto.notes());
        pot.setPlanNumber(dto.planNumber());
        pot.setSchemeNumber(dto.schemeNumber());
        if (dto.providerId() != null) {
            pot.setProvider(providerRepo.findById(dto.providerId()).orElse(null));
        } else {
            pot.setProvider(null);
        }
        return pot;
    }

    private static Snapshot fromSnapshotDto(SnapshotDto dto, PotRepo potRepo) {
        Snapshot s = new Snapshot();
        s.setId(dto.id());
        if (dto.potId() != null) {
            Pot pot = potRepo.findById(dto.potId()).orElse(null);
            s.setPot(pot);
        }
        s.setDate(dto.date());
        s.setBalance(dto.balance());
        s.setSource(dto.source());
        s.setNote(dto.note());
        return s;
    }

    private static Transaction fromTransactionDto(TransactionDto dto, PotRepo potRepo) {
        Transaction t = new Transaction();
        t.setId(dto.id());
        if (dto.potId() != null) {
            Pot pot = potRepo.findById(dto.potId()).orElse(null);
            t.setPot(pot);
        }
        t.setDate(dto.date());
        t.setType(dto.type());
        t.setAmount(dto.amount());
        t.setNote(dto.note());
        return t;
    }
}

