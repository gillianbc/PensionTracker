package com.gillianbc.pensionstracker.repo;

import com.gillianbc.pensionstracker.model.Snapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SnapshotRepo extends JpaRepository<Snapshot,Long>{
    List<Snapshot> findByPotIdOrderByDate(Long potId);
}
