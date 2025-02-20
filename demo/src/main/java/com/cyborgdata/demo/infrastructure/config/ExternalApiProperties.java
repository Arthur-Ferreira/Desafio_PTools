package com.cyborgdata.demo.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "external.api")
public class ExternalApiProperties {
  private String baseUrl = "https://developtecnologi2.websiteseguro.com";
  private int timeout = 5000;
  private int retryAttempts = 3;
  private int retryDelay = 1000;
}
