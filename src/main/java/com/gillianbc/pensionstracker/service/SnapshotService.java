package com.gillianbc.pensionstracker.service;

import com.gillianbc.pensionstracker.model.Pot;
import com.gillianbc.pensionstracker.model.Snapshot;
import com.gillianbc.pensionstracker.repo.PotRepo;
import com.gillianbc.pensionstracker.repo.SnapshotRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SnapshotService {

    private final PotRepo potRepository;
    private final SnapshotRepo snapshotRepository;

    public SnapshotService(PotRepo potRepository, SnapshotRepo snapshotRepository) {
        this.potRepository = potRepository;
        this.snapshotRepository = snapshotRepository;
    }

    @Transactional
    public void saveSnapshot(Long potId, LocalDate date, double balance) {
        Pot pot = potRepository.findById(potId)
                .orElseThrow(() -> new IllegalArgumentException("Pot not found: " + potId));
        Snapshot snapshot = new Snapshot();
        snapshot.setPot(pot);
        snapshot.setDate(date);
        snapshot.setBalance(balance);
        snapshotRepository.save(snapshot);
    }
}
