package com.gillianbc.pensionstracker.controller;

import com.gillianbc.pensionstracker.service.DatabaseMaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminMaintenanceController {

    private final DatabaseMaintenanceService databaseMaintenanceService;

    @PostMapping("/clear")
    public ResponseEntity<Void> clearDatabase() {
        databaseMaintenanceService.clearAllData();
        return ResponseEntity.noContent().build();
    }
}
