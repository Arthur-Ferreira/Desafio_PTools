package com.cyborgdata.demo.api.dto;

import com.cyborgdata.demo.api.dto.response.CompanyDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExternalPendingResponseDto {
  private boolean success;
  private String message;
  private List<CompanyDto> data;
}
