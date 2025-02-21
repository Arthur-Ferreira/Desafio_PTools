package com.cyborgdata.demo.utils;

import com.cyborgdata.demo.api.dto.response.EmployeeDto;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class CsvParser {

  public List<EmployeeDto> parseEmployees(String filePath) {
    try (FileReader reader = new FileReader(filePath)) {
      log.info("Parsing CSV file: {}", filePath);

      List<EmployeeDto> employees = new CsvToBeanBuilder<EmployeeDto>(reader)
          .withType(EmployeeDto.class)
          .withSeparator(',')
          .withIgnoreLeadingWhiteSpace(true)
          .build()
          .parse();

      log.info("Successfully parsed {} employees from CSV", employees.size());
      return employees;
    } catch (Exception e) {
      log.error("Error parsing CSV file: {}", filePath, e);
      return Collections.emptyList();
    }
  }
}
