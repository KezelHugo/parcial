package com.msorders.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponseDTO {
  private Long productId;
  private String name;
  private Double price;
  private Integer quantity;
}
