package com.cyborgdata.demo.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingStatusDto {
  private String company;
  private String runtime;
  private String processing;
  private String status;

  public enum Status {
    PENDING("Pending"),
    RUNNING("Running"),
    COMPLETED("Completed");

    private final String value;

    Status(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }
}