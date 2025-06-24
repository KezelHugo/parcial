package com.msorders.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.msorders.exception.ProductNotFoundException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductClient {

  private final RestTemplate restTemplate;

  private static final String BASE_URL = "http://localhost:8080/products";

  private static final String CIRCUIT_BREAKER_NAME = "productService";

  @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "fallbackValidateProduct")
  public ProductInfo validateProduct(Long productId, int quantity) {
    String url = BASE_URL + "/" + productId + "/validate?quantity=" + quantity;
    return restTemplate.getForObject(url, ProductInfo.class);
  }

  @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "fallbackDecreaseStock")
  public void decreaseStock(Long productId, int quantity) {
    String url = BASE_URL + "/" + productId + "/decrease-stock?quantity=" + quantity;
    restTemplate.put(url, null);
  }

  // --------------------------
  // Métodos fallback
  // --------------------------

  public ProductInfo fallbackValidateProduct(Long productId, Throwable t) {
  log.error("Fallo en consulta de producto ID {}: {}", productId, t.getMessage());

  if (t.getMessage() != null && t.getMessage().contains("404")) {
    throw new ProductNotFoundException("Producto no encontrado con ID: " + productId);
  }

  ProductInfo fallback = new ProductInfo();
  fallback.setId(productId);
  fallback.setName("Producto no disponible");
  fallback.setPrice(0.0);
  fallback.setStockAvailable(false);
  fallback.setFallback(true);
  return fallback;
}

  public void fallbackDecreaseStock(Long productId, Throwable ex) {
    log.error("Fallo al disminuir stock del producto {}: {}", productId, ex.getMessage());
    // No hace nada, solo loguea para evitar que caiga el servicio
  }

  @Data
  public static class ProductInfo {
    private Long id;
    private String name;
    private Double price;
    private boolean stockAvailable;
    private Integer currentStock;
    private boolean fallback;
  }
}