package com.gillianbc.pensionstracker.dto;

import java.time.LocalDate;

public record PotReportDto(
        Long potId,
        LocalDate fromDate,
        LocalDate toDate,
        double openingBalance,
        double currentBalance,
        double contributionsExclRebates,
        double contributionsInclRebates,
        double netFlows,
        double growth,
        Double irrAnnualPercent // null if not computable
) {}

