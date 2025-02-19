package com.cyborgdata.demo.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Company {
  private String hash;
  private String pais;
  private String sigla;
  private String company;
  private String email;
  private String cnpj;
  private DocumentosWrapper documentos;
}
