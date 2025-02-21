package com.cyborgdata.demo.service;

import com.cyborgdata.demo.api.dto.external.ExternalApiResponse;
import com.cyborgdata.demo.api.dto.response.CompanyDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiService {

  private final RestTemplate restTemplate;
  private final ProcessingService processingService;

  @Value("${api.external.pending-url}")
  private String pendingApiUrl;

  public List<CompanyDto> fetchPendingCompanies() {
    try {
      disableSSLCertificateValidation();

      log.info("Fetching data from external API: {}", pendingApiUrl);
      ExternalApiResponse response = restTemplate.getForObject(pendingApiUrl, ExternalApiResponse.class);

      if (response == null || !response.isSuccess() || response.getData() == null) {
        log.warn("Failed to fetch data or empty response from external API");
        return Collections.emptyList();
      }

      List<CompanyDto> companies = response.getData().stream()
          .map(company -> {
            CompanyDto dto = new CompanyDto(company.getHash(), company.getCompany());
            // Start processing this company in the background
            processingService.startProcessing(company);
            return dto;
          })
          .collect(Collectors.toList());

      log.info("Successfully fetched {} companies from external API", companies.size());
      return companies;

    } catch (Exception e) {
      log.error("Error fetching data from external API", e);
      return Collections.emptyList();
    }
  }

  private void disableSSLCertificateValidation() {
    try {
      TrustManager[] trustAllCerts = new TrustManager[]{
          new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {}
            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {}
            @Override
            public X509Certificate[] getAcceptedIssuers() {
              return new X509Certificate[0];
            }
          }
      };

      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    } catch (Exception e) {
      log.error("Failed to disable SSL certificate validation", e);
    }
  }

}
