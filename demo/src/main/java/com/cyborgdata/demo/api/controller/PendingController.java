package com.cyborgdata.demo.api.controller;

import com.cyborgdata.demo.api.dto.response.CompanyDto;
import com.cyborgdata.demo.service.ApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pendente")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Pending API", description = "Endpoints for retrieving pending data")
public class PendingController {

  private final ApiService apiService;

  @GetMapping
  @Operation(summary = "Get all pending companies", description = "Retrieve a list of pending companies from the external API")
  public ResponseEntity<List<CompanyDto>> getPendingCompanies() {
    log.info("Received request to get pending companies");
    List<CompanyDto> companies = apiService.fetchPendingCompanies();
    return ResponseEntity.ok(companies);
  }
}
