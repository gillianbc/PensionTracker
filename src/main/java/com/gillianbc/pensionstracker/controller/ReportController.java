package com.gillianbc.pensionstracker.controller;

import com.gillianbc.pensionstracker.dto.PotReportDto;
import com.gillianbc.pensionstracker.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/{potId}")
    public PotReportDto getReport(@PathVariable Long potId) {
        PotReportDto response = reportService.buildReport(potId);
        if (response == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return response;
    }
}
