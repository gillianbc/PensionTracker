package com.gillianbc.pensionstracker.controller;

import com.gillianbc.pensionstracker.service.SnapshotService;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/snapshots")
public class SnapshotController {

    private final SnapshotService snapshotService;

    public SnapshotController(SnapshotService snapshotService) {
        this.snapshotService = snapshotService;
    }

    /**
     * Imports snapshot data from an Excel (.xlsx) spreadsheet file into the specified pot.
     * <p>
     * The spreadsheet should have the following format (first row as header):
     * <pre>
     * |   date     |  balance   |
     * |------------|------------|
     * | 2024-01-01 | 100000.00  |
     * | 2024-02-01 | 101500.27  |
     * </pre>
     * <ul>
     *     <li><b>date</b>: String in YYYY-MM-DD format</li>
     *     <li><b>balance</b>: Numeric value (may contain a comma for thousands, e.g., 101,239.27)</li>
     * </ul>
     *
     * <p>Sample HTTP Request:</p>
     * <pre>
     * POST /api/snapshots/import-excel/103
     * Content-Type: application/json
     *
     * {
     *   "excelPath": "/absolute/path/to/snapshots.xlsx"
     * }
     * </pre>
     *
     * @param potId   The ID of the pot into which the snapshots will be imported.
     * @param request JSON payload providing the Excel file's absolute path.
     * @return Summary of imported rows, or error details.
     */
    @PostMapping("/import-excel/{potId}")
    public ResponseEntity<?> importSnapshotsFromExcel(
            @PathVariable("potId") Long potId,
            @RequestBody ExcelPathRequest request
    ) {
        String excelPath = request.getExcelPath();
        if (excelPath == null || excelPath.isBlank()) {
            return ResponseEntity.badRequest().body("Excel path is required.");
        }
        List<String> errors = new ArrayList<>();
        int imported = 0;
        try (FileInputStream fis = new FileInputStream(excelPath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            for (Row row : sheet) {
                // Skip the header row if present (e.g., first row with "date", "balance")
                if (row.getRowNum() == 0) {
                    String header0 = formatter.formatCellValue(row.getCell(0)).toLowerCase();
                    String header1 = formatter.formatCellValue(row.getCell(1)).toLowerCase();
                    if ("date".equals(header0) && "balance".equals(header1)) {
                        continue;
                    }
                }
                String dateStr = formatter.formatCellValue(row.getCell(0));
                String balanceStr = formatter.formatCellValue(row.getCell(1));
                try {
                    LocalDate date = LocalDate.parse(dateStr);
                    double balance = Double.parseDouble(balanceStr.replace(",", ""));
                    snapshotService.saveSnapshot(potId, date, balance);
                    imported++;
                } catch (Exception e) {
                    errors.add("Row " + (row.getRowNum() + 1) + " error: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to read Excel file: " + e.getMessage());
        }

        return ResponseEntity.ok("Imported " + imported + " snapshots for pot " + potId +
                (errors.isEmpty() ? "" : "; Errors: " + String.join(" | ", errors)));
    }

    @Data
    public static class ExcelPathRequest {
        @NotBlank
        private String excelPath;
    }
}
