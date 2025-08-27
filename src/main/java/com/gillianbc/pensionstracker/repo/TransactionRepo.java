package com.gillianbc.pensionstracker.repo;

import com.gillianbc.pensionstracker.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction,Long> {
    List<Transaction> findByPotIdOrderByDate(Long potId);
}
