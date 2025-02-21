package com.cyborgdata.demo.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
  private List<CountryReport> empresas;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CountryReport {
    private String country;
    private int count_employee;
    private String size_file;
    private String runtime;
    private CityEmployeeCount count_employee_city;
    private StateEmployeeCount count_employee_state;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CityEmployeeCount {
    private String city;
    private String state;
    private int kids;
    private int young;
    private int adult;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class StateEmployeeCount {
    private String state;
    private int kids;
    private int young;
    private int adult;
  }
}