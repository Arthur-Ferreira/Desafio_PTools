package com.cyborgdata.demo.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileDownloader {

  private final HttpClient httpClient;

  public FileDownloader() {
    // Initialize HttpClient with SSL settings
    this.httpClient = HttpClient.newBuilder()
        .sslContext(createInsecureSslContext()) // Insecure context
        .connectTimeout(Duration.ofSeconds(10)) // Customize timeout
        .build();
  }


  public void downloadFile(String url, String destinationPath) throws IOException, InterruptedException {
    log.info("Downloading file from URL: {} to path: {}", url, destinationPath);

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .GET()
        .build();

    Path targetPath = Paths.get(destinationPath);
    HttpResponse<Path> response = httpClient.send(request,
        HttpResponse.BodyHandlers.ofFile(targetPath));

    if (response.statusCode() >= 200 && response.statusCode() < 300) {
      log.info("File downloaded successfully, size: {} bytes", Files.size(targetPath));
    } else {
      throw new IOException("Failed to download file, status code: " + response.statusCode());
    }
  }

  private SSLContext createInsecureSslContext() {
    try {
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, new TrustManager[]{new InsecureTrustManager()}, new SecureRandom());
      return sslContext;
    } catch (Exception e) {
      throw new RuntimeException("Failed to create insecure SSL context", e);
    }
  }

  private static class InsecureTrustManager implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[0];
    }
  }

}

