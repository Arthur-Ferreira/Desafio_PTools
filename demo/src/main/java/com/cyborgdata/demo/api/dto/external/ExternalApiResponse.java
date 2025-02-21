package com.cyborgdata.demo.api.dto.external;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ExternalApiResponse {
  private boolean success;
  private String message;
  private List<CompanyData> data;

  @Data
  public static class CompanyData {
    private String hash;
    private String pais;
    private String sigla;
    private String company;
    private String email;
    private String cnpj;
    private Map<String, String> documentos;
  }
}
