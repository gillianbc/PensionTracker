package com.gillianbc.pensionstracker.controller;

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

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
class ApiController {
    private final ProviderRepo providers;
    private final PotRepo pots;
    private final SnapshotRepo snaps;
    private final TransactionRepo txs;

    @GetMapping("/pots")
    List<Pot> listPots() {
        return pots.findAll();
    }

    @PostMapping("/pots")
    Pot addPot(@RequestBody Pot p) {
        return pots.save(p);
    }

    @PostMapping("/providers")
    Provider addProvider(@RequestBody Provider p) {
        return providers.save(p);
    }

    @GetMapping("/providers")
    List<Provider> listProviders() {
        return providers.findAll();
    }

    @PostMapping("/snapshots")
    Snapshot addSnap(@RequestBody Snapshot s) {
        return snaps.save(s);
    }

    @PostMapping("/transactions")
    Transaction addTx(@RequestBody Transaction t) {
        return txs.save(t);
    }
}

