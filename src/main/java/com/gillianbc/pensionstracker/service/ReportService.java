package com.gillianbc.pensionstracker.service;

import com.gillianbc.pensionstracker.dto.PotReportDto;
import com.gillianbc.pensionstracker.model.Snapshot;
import com.gillianbc.pensionstracker.model.Transaction;
import com.gillianbc.pensionstracker.repo.PotRepo;
import com.gillianbc.pensionstracker.repo.SnapshotRepo;
import com.gillianbc.pensionstracker.repo.TransactionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.isFinite;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final SnapshotRepo snapshotRepo;
    private final TransactionRepo txRepo;
    private final PotRepo potRepo;

    public PotReportDto buildReport(Long potId) {
        // Check if the pot exists; if not, return null (for 404 handling)
        if (!potExists(potId)) {
            return null;
        }
        // 1) Get data
        List<Snapshot> snaps = snapshotRepo.findByPotIdOrderByDate(potId);
        if (snaps.isEmpty()) {
            return new PotReportDto(potId, null, null, 0, 0, 0, 0, 0, 0, null);
        }
        LocalDate from = snaps.get(0).getDate();
        LocalDate to   = snaps.get(snaps.size()-1).getDate();
        double opening = snaps.get(0).getBalance();
        double current = snaps.get(snaps.size()-1).getBalance();

        List<Transaction> txs = txRepo.findByPotIdOrderByDate(potId);

        // 2) Totals
        double contribExclRebates = txs.stream()
                .filter(t -> isInflow(t) && !isRebate(t))
                .mapToDouble(Transaction::getAmount).sum();

        double contribInclRebates = txs.stream()
                .filter(this::isInflowOrRebate)
                .mapToDouble(Transaction::getAmount).sum();

        double netFlows = txs.stream()
                .mapToDouble(Transaction::getAmount).sum();

        // 3) Growth since first snapshot
        // balance_now = opening + netFlows + growth  => growth = balance_now - opening - netFlows
        double growth = current - opening - netFlows;

        // 4) IRR (date-weighted). For IRR:
        // - Outgoing cash (your money invested) = negative numbers on their dates.
        // - Incoming cash (withdrawals/fees negative in DB -> we flip sign) = positive numbers.
        // - Add terminal positive flow = current balance at 'to' date.
        List<LocalDate> dates = new ArrayList<>();
        List<Double> flows = new ArrayList<>();

        for (Transaction t : txs) {
            // Treat inflows to the pot (contribution/employer_match/transfer_in/rebate) as NEGATIVE cash from your pocket.
            if (isInflowOrRebate(t)) {
                dates.add(t.getDate());
                flows.add(-t.getAmount());
            } else {
                // Outflows from the pot (withdrawal/fee/transfer_out) are cash back to you: POSITIVE
                dates.add(t.getDate());
                flows.add(+t.getAmount()); // the amount should be negative in DB for fee/withdrawal; this will subtract -> positive
            }
        }
        // Terminal value: you "sell" the pot at the last snapshot date
        dates.add(to);
        flows.add(+current);

        Double irr = xirr(dates, flows);

        return new PotReportDto(
                potId, from, to, opening, current,
                round(contribExclRebates), round(contribInclRebates),
                round(netFlows), round(growth),
                irr == null ? null : round(irr * 100.0)
        );
    }

    /**
     * Checks if a pot with the given ID exists in the database.
     *
     * @param potId the ID of the pot to check for existence
     * @return true if the pot exists, false otherwise
     */
    private boolean potExists(Long potId) {
        return potRepo.existsById(potId);
    }

    private boolean isRebate(Transaction t) {
        return "rebate".equalsIgnoreCase(t.getType());
    }
    private boolean isInflow(Transaction t) {
        return switch (t.getType().toLowerCase()) {
            case "contribution", "employer_match", "transfer_in" -> true;
            default -> false;
        };
    }
    private boolean isInflowOrRebate(Transaction t) {
        return isInflow(t) || isRebate(t);
    }
    private double round(double v) { return Math.round(v * 100.0) / 100.0; }

    /** XIRR using Newton-Raphson. Returns annual rate (e.g., 0.07 for 7%) or null if not solvable. */
    private Double xirr(List<LocalDate> dates, List<Double> flows) {
        if (flows.isEmpty() || flows.stream().allMatch(f -> f == 0.0)) return null;
        // Need at least one negative and one positive to solve
        boolean hasNeg = flows.stream().anyMatch(f -> f < 0);
        boolean hasPos = flows.stream().anyMatch(f -> f > 0);
        if (!hasNeg || !hasPos) return null;

        LocalDate d0 = dates.get(0);
        double rate = 0.07; // initial guess 7%
        for (int i = 0; i < 100; i++) {
            double f = 0.0;
            double df = 0.0;
            for (int k = 0; k < flows.size(); k++) {
                double days = (dates.get(k).toEpochDay() - d0.toEpochDay());
                double years = days / 365.0;
                double denom = Math.pow(1.0 + rate, years);
                f += flows.get(k) / denom;
                if (years != 0) {
                    df += -years * flows.get(k) / (denom * (1.0 + rate));
                }
            }
            if (Math.abs(df) < 1e-12) break;
            double newRate = rate - f / df;
            if (Math.abs(newRate - rate) < 1e-7) return newRate;
            rate = newRate;
        }
        return isFinite(rate) ? rate : null;
    }
}

