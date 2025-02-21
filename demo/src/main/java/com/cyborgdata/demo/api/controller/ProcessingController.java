package com.cyborgdata.demo.api.controller;

import com.cyborgdata.demo.api.dto.response.ProcessingStatusDto;
import com.cyborgdata.demo.service.ProcessingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lot")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Processing API", description = "Endpoints for checking processing status")
public class ProcessingController {

  private final ProcessingService processingService;

  @GetMapping("/{hash}")
  @Operation(summary = "Get processing status by hash", description = "Retrieve the processing status of a company by its hash")
  public ResponseEntity<ProcessingStatusDto> getProcessingStatus(
      @Parameter(description = "Company hash identifier") @PathVariable String hash) {
    log.info("Received request to get processing status for hash: {}", hash);
    ProcessingStatusDto status = processingService.getProcessingStatus(hash);
    return ResponseEntity.ok(status);
  }
}
