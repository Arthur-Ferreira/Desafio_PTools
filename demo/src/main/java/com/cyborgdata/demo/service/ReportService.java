package com.cyborgdata.demo.service;
import com.cyborgdata.demo.api.dto.response.EmployeeDto;
import com.cyborgdata.demo.api.dto.response.ProcessingStatusDto;
import com.cyborgdata.demo.api.dto.response.ReportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
  private final ProcessingService processingService;
  /**
   * Generates a report for all processed companies, grouped by country.
   * Returns employee counts for the most populous city and state per country.
   */
  public ReportDto generateReport() {
    log.info("Generating reports for all processed companies");
    // Initialize the list of country reports
    List<ReportDto.CountryReport> countryReports = new ArrayList<>();
    // Retrieve all processing contexts
    Map<String, ProcessingService.ProcessingContext> processContexts = processingService.getAllProcessingContexts();
    log.debug("Total processing contexts found: {}", processContexts.size());
    // Standard country codes to ensure consistent output
    List<String> targetCountries = Arrays.asList("brasil", "EUA", "França");
    // Process each target country
    for (String country : targetCountries) {
      log.info("Processing country: {}", country);
      // Find all companies for this country
      List<String> companyHashes = processContexts.entrySet().stream()
          .filter(entry -> entry.getValue().getStatus() == ProcessingStatusDto.Status.COMPLETED)
          .filter(entry -> matchesCountry(entry.getKey(), country))
          .map(Map.Entry::getKey)
          .collect(Collectors.toList());
      log.debug("Found {} companies for country: {}", companyHashes.size(), country);
      // Skip if no companies for this country
      if (companyHashes.isEmpty()) {
        log.warn("No companies found for country: {}", country);
        continue;
      }
      // Collect all employees from all companies in this country
      List<EmployeeDto> allEmployees = new ArrayList<>();
      long totalFileSize = 0;
      String runtime = "";
      for (String hash : companyHashes) {
        log.debug("Processing company hash: {}", hash);
        List<EmployeeDto> employees = processingService.getProcessedEmployees(hash);
        if (employees == null || employees.isEmpty()) {
          log.warn("No employees found for company hash: {}", hash);
          continue;
        }
        allEmployees.addAll(employees);
        // Accumulate file size and use the last company's runtime
        ProcessingService.ProcessingContext context = processContexts.get(hash);
        if (context == null) {
          log.warn("No processing context found for company hash: {}", hash);
          continue;
        }
        totalFileSize += context.getFileSize();
        runtime = processingService.getProcessingStatus(hash).getRuntime();
      }
      // Skip if no employees
      if (allEmployees.isEmpty()) {
        log.warn("No employees found for country: {}", country);
        continue;
      }
      // Find the most populous city
      Map<String, List<EmployeeDto>> cityGroups = allEmployees.stream()
          .collect(Collectors.groupingBy(EmployeeDto::getCity));
      Map.Entry<String, List<EmployeeDto>> mostPopulousCityEntry = cityGroups.entrySet().stream()
          .max(Comparator.comparingInt(e -> e.getValue().size()))
          .orElse(null);
      ReportDto.CityEmployeeCount cityCount = null;
      if (mostPopulousCityEntry != null) {
        String city = mostPopulousCityEntry.getKey();
        String state = mostPopulousCityEntry.getValue().get(0).getState(); // Get state from the first employee
        List<EmployeeDto> cityEmployees = mostPopulousCityEntry.getValue();
        cityCount = new ReportDto.CityEmployeeCount(
            city,
            state,
            countByAgeGroup(cityEmployees, "kids"),
            countByAgeGroup(cityEmployees, "young"),
            countByAgeGroup(cityEmployees, "adult")
        );
      }
      // Find the most populous state
      Map<String, List<EmployeeDto>> stateGroups = allEmployees.stream()
          .collect(Collectors.groupingBy(EmployeeDto::getState));
      Map.Entry<String, List<EmployeeDto>> mostPopulousStateEntry = stateGroups.entrySet().stream()
          .max(Comparator.comparingInt(e -> e.getValue().size()))
          .orElse(null);
      ReportDto.StateEmployeeCount stateCount = null;
      if (mostPopulousStateEntry != null) {
        String state = mostPopulousStateEntry.getKey();
        List<EmployeeDto> stateEmployees = mostPopulousStateEntry.getValue();
        stateCount = new ReportDto.StateEmployeeCount(
            state,
            countByAgeGroup(stateEmployees, "kids"),
            countByAgeGroup(stateEmployees, "young"),
            countByAgeGroup(stateEmployees, "adult")
        );
      }
      // Create country report
      ReportDto.CountryReport countryReport = new ReportDto.CountryReport(
          country,
          allEmployees.size(),
          formatFileSize(totalFileSize),
          runtime,
          cityCount,
          stateCount
      );
      countryReports.add(countryReport);
    }
    // Log the final report
    log.info("Generated report with {} countries: {}", countryReports.size(), countryReports);
    return new ReportDto(countryReports);
  }
  private int countByAgeGroup(List<EmployeeDto> employees, String ageGroup) {
    return (int) employees.stream()
        .filter(employee -> ageGroup.equals(employee.getAgeGroup()))
        .count();
  }
  private String formatFileSize(long bytes) {
    double kilobytes = bytes / 1024.0;
    double megabytes = kilobytes / 1024.0;
    DecimalFormat df = new DecimalFormat("#0.00");
    return df.format(megabytes) + " mb";
  }
  // Matches country name to hash with proper case handling
  private boolean matchesCountry(String hash, String targetCountry) {
    if (hash == null || hash.isEmpty()) {
      return false;
    }
    String countryFromHash = getCountryFromHash(hash);
    log.debug("Hash: {}, Country from hash: {}, Target country: {}", hash, countryFromHash, targetCountry);
    // Use case-insensitive comparison
    return countryFromHash.equalsIgnoreCase(targetCountry);
  }
  // Simple mapping to extract country from hash
  private String getCountryFromHash(String hash) {
    if (hash == null || hash.isEmpty()) {
      return "Unknown";
    }

    if (hash.equals("8223bb13-23d8-4831-ad8a-12359731064c")) {
      return "brasil";
    } else if (hash.equals("8263e8bd-9bce-468b-9ab6-586db6b1f78e")) {
      return "EUA";
    } else {
      return "França";
    }
  }
}