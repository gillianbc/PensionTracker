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
        double growth,
        Double cagrAnnualPercent, // null if not computable
        double growthPercent
) {
    public PotReportDto(
            Long potId,
            LocalDate fromDate,
            LocalDate toDate,
            double openingBalance,
            double currentBalance,
            double contributionsExclRebates,
            double contributionsInclRebates,
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
                growth,
                computeCAGR(openingBalance, currentBalance, fromDate, toDate),
                computeGrowthPercent(openingBalance, currentBalance, contributionsInclRebates)
                
        );
    }

    private static Double computeGrowthPercent(double openingBalance, double currentBalance, double contributionsInclRebates) {
        if (openingBalance <= 0) {
            return null;
        }
        double growth = currentBalance - openingBalance - contributionsInclRebates;
        return Math.round((growth / openingBalance * 100.0) ) / 100.0;
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
        return Math.round((Math.pow(currentBalance / openingBalance, 1 / years) - 1) * 100.0) / 100.0; // CAGR formula rounded to 2 decimal places
    }
}

