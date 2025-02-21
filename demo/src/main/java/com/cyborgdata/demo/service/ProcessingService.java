package com.cyborgdata.demo.service;

import com.cyborgdata.demo.api.dto.external.ExternalApiResponse;
import com.cyborgdata.demo.api.dto.response.EmployeeDto;
import com.cyborgdata.demo.api.dto.response.ProcessingStatusDto;
import com.cyborgdata.demo.utils.CsvParser;
import com.cyborgdata.demo.utils.FileDownloader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessingService {

  private final FileDownloader fileDownloader;
  private final CsvParser csvParser;

  @Value("${processing.file.temp-dir}")
  private String tempDir;

  // In-memory storage for processing status
  private final Map<String, ProcessingContext> processingStatuses = new ConcurrentHashMap<>();

  // In-memory storage for processed employee data
  private final Map<String, List<EmployeeDto>> processedEmployees = new ConcurrentHashMap<>();

  @Async("processingTaskExecutor")
  public void startProcessing(ExternalApiResponse.CompanyData company) {
    String hash = company.getHash();
    log.info("Starting processing for company: {} with hash: {}", company.getCompany(), hash);

    // Initialize processing context
    ProcessingContext context = new ProcessingContext();
    context.setStartTime(Instant.now());
    context.setCompanyName(company.getCompany());
    context.setStatus(ProcessingStatusDto.Status.PENDING);
    processingStatuses.put(hash, context);

    try {
      // Update status to RUNNING
      context.setStatus(ProcessingStatusDto.Status.RUNNING);

      // Get document URL
      String documentUrl = company.getDocumentos().get("funcionario");
      if (documentUrl == null || documentUrl.isEmpty()) {
        throw new IllegalStateException("Document URL not found for company: " + company.getCompany());
      }

      // Create temp directory if it doesn't exist
      Files.createDirectories(Paths.get(tempDir));

      // Download CSV file
      context.setProgress(10);
      String filePath = tempDir + File.separator + hash + ".csv";
      fileDownloader.downloadFile(documentUrl, filePath);

      // Parse CSV file
      context.setProgress(30);
      List<EmployeeDto> employees = csvParser.parseEmployees(filePath);

      // Process data
      context.setProgress(60);
      // Simulate some processing time
      Thread.sleep(5000);

      // Store processed data for reporting
      processedEmployees.put(hash, employees);

      // Calculate file size
      long fileSize = Files.size(Path.of(filePath));
      context.setFileSize(fileSize);

      // Update progress
      context.setProgress(100);
      context.setStatus(ProcessingStatusDto.Status.COMPLETED);
      context.setEndTime(Instant.now());

      log.info("Completed processing for company: {} with hash: {}", company.getCompany(), hash);
    } catch (Exception e) {
      log.error("Error processing company data for hash: {}", hash, e);
      context.setStatus(ProcessingStatusDto.Status.COMPLETED);
      context.setErrorMessage(e.getMessage());
      context.setEndTime(Instant.now());
    }
  }

  public ProcessingStatusDto getProcessingStatus(String hash) {
    ProcessingContext context = processingStatuses.getOrDefault(hash,
        createDefaultContext("Unknown Company"));

    return ProcessingStatusDto.builder()
        .company(context.getCompanyName())
        .processing(context.getProgress() + "%")
        .runtime(calculateRuntime(context))
        .status(context.getStatus().getValue())
        .build();
  }

  public List<EmployeeDto> getProcessedEmployees(String hash) {
    return processedEmployees.getOrDefault(hash, Collections.emptyList());
  }

  public Map<String, ProcessingContext> getAllProcessingContexts() {
    return Collections.unmodifiableMap(processingStatuses);
  }

  private String calculateRuntime(ProcessingContext context) {
    Instant endTime = context.getEndTime() != null ? context.getEndTime() : Instant.now();
    long minutes = Duration.between(context.getStartTime(), endTime).toMinutes();
    return minutes + " minutos";
  }

  private ProcessingContext createDefaultContext(String companyName) {
    ProcessingContext context = new ProcessingContext();
    context.setCompanyName(companyName);
    context.setStartTime(Instant.now());
    context.setStatus(ProcessingStatusDto.Status.PENDING);
    context.setProgress(0);
    return context;
  }

  @Getter
  public static class ProcessingContext {
    // Getters and setters
    private String companyName;
    private Instant startTime;
    private Instant endTime;
    private ProcessingStatusDto.Status status;
    private int progress;
    private String errorMessage;
    private long fileSize;

    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public void setStartTime(Instant startTime) { this.startTime = startTime; }

    public void setEndTime(Instant endTime) { this.endTime = endTime; }

    public void setStatus(ProcessingStatusDto.Status status) { this.status = status; }

    public void setProgress(int progress) { this.progress = progress; }

    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
  }
}

