package com.gillianbc.pensionstracker.service;

import com.gillianbc.pensionstracker.repo.PotRepo;
import com.gillianbc.pensionstracker.repo.ProviderRepo;
import com.gillianbc.pensionstracker.repo.SnapshotRepo;
import com.gillianbc.pensionstracker.repo.TransactionRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
// Import other repositories as needed

@Service
@RequiredArgsConstructor
public class DatabaseMaintenanceService {

    private final TransactionRepo transactionRepo;
    private final SnapshotRepo snapshotRepo;
    private final PotRepo potRepo;
    private final ProviderRepo providerRepo;

    @Transactional
    public void clearAllData() {
        // Delete all rows from child/linked tables before parents, if applicable.
        transactionRepo.deleteAll();
        snapshotRepo.deleteAll();
        potRepo.deleteAll();
        providerRepo.deleteAll();
    }

    public void clearPots() {
        potRepo.deleteAll();
    }
}
