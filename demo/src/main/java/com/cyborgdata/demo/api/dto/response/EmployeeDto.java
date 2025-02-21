package com.cyborgdata.demo.api.dto.response;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Data
public class EmployeeDto {
  private static final Logger logger = Logger.getLogger(EmployeeDto.class.getName());

  @CsvBindByName(column = "Full Name")
  private String fullName;

  @CsvBindByName(column = "SSN")
  private String ssn;

  @CsvBindByName(column = "Email")
  private String email;

  @CsvBindByName(column = "Phone")
  private String phone;

  @CsvBindByName(column = "Address")
  private String address;

  @CsvBindByName(column = "City")
  private String city;

  @CsvBindByName(column = "State")
  private String state;

  @CsvBindByName(column = "Zip Code")
  private String zipCode;

  @CsvBindByName(column = "Date of Birth")
  private String dateOfBirth;

  // Helper method to calculate age
  public int getAge() {
    if (dateOfBirth == null || dateOfBirth.trim().isEmpty()) {
      logger.warning("Empty date of birth for employee: " + fullName);
      return 0;
    }

    // Try multiple date formats
    List<DateTimeFormatter> formatters = Arrays.asList(
        DateTimeFormatter.ofPattern("M/d/yyyy"),
        DateTimeFormatter.ofPattern("d/M/yyyy"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd")
    );

    for (DateTimeFormatter formatter : formatters) {
      try {
        LocalDate birthDate = LocalDate.parse(dateOfBirth, formatter);
        LocalDate now = LocalDate.now();
        int age = now.getYear() - birthDate.getYear();

        // Adjust age if birthday hasn't occurred yet this year
        if (now.getMonthValue() < birthDate.getMonthValue() ||
            (now.getMonthValue() == birthDate.getMonthValue() && now.getDayOfMonth() < birthDate.getDayOfMonth())) {
          age--;
        }

        // Log successful parsing for debugging
        logger.fine("Successfully parsed date " + dateOfBirth + " as " + birthDate + ", age: " + age);
        return age;
      } catch (DateTimeParseException e) {
        // Continue to next formatter
      }
    }

    // If all parsing attempts fail, log and return default
    logger.warning("Failed to parse date of birth: " + dateOfBirth + " for employee: " + fullName);
    return 0;
  }


  public String getAgeGroup() {
    int age = getAge();
    if (age < 18) return "kids";
    if (age >= 18 && age <= 30) return "young";
    return "adult";
  }
}