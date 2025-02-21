package com.cyborgdata.demo.api.controller;

import com.cyborgdata.demo.api.dto.response.ReportDto;
import com.cyborgdata.demo.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rel")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Report API", description = "Endpoints for generating reports")
public class ReportController {

  private final ReportService reportService;

  @GetMapping
  @Operation(summary = "Generate company reports", description = "Generate detailed reports for all processed companies")
  public ResponseEntity<ReportDto> generateReports() {
    log.info("Received request to generate reports");
    ReportDto report = reportService.generateReport();
    return ResponseEntity.ok(report);
  }
}