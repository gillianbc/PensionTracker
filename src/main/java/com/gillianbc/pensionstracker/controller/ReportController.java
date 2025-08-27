package com.gillianbc.pensionstracker.controller;

import com.gillianbc.pensionstracker.dto.PotReportDto;
import com.gillianbc.pensionstracker.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/{potId}")
    public PotReportDto getReport(@PathVariable Long potId) {
        return reportService.buildReport(potId);
    }
}
