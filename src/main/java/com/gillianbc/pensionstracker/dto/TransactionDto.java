package com.gillianbc.pensionstracker.dto;

import java.time.LocalDate;

public record TransactionDto(
        Long id,
        Long potId,
        LocalDate date,
        String type,
        Double amount,
        String note
) {}
