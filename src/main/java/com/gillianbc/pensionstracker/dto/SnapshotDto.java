package com.gillianbc.pensionstracker.dto;

import java.time.LocalDate;

public record SnapshotDto(
        Long id,
        Long potId,
        LocalDate date,
        Double balance,
        String source,
        String note
) {}
