package com.gillianbc.pensionstracker.dto;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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
        Double cagrAnnualPercent // null if not computable
) {
    public PotReportDto(
            Long potId,
            LocalDate fromDate,
            LocalDate toDate,
            double openingBalance,
            double currentBalance,
            double contributionsExclRebates,
            double contributionsInclRebates,
            double netFlows,
            double growth
    ) {
        this(
                potId,
                fromDate,
                toDate,
                openingBalance,
                currentBalance,
                contributionsExclRebates,
                contributionsInclRebates,
                netFlows,
                growth,
                computeCAGR(openingBalance, currentBalance, fromDate, toDate)
        );
    }

    private static Double computeCAGR(double openingBalance, double currentBalance, LocalDate fromDate, LocalDate toDate) {
        if (openingBalance <= 0 || currentBalance <= 0) {
            return null; // Cannot compute CAGR with non-positive balances
        }
        if (fromDate == null || toDate == null) {
            return null; // Dates must be valid and non-null
        }
        if (!toDate.isAfter(fromDate)) {
            return null; // `toDate` must be after `fromDate`
        }

        long days = ChronoUnit.DAYS.between(fromDate, toDate);
        if (days <= 0) {
            return null; // Calculation timeframe is invalid
        }

        double years = days / 365.0; // Convert days to years
        return Math.pow(currentBalance / openingBalance, 1 / years) - 1; // CAGR formula
    }
}

