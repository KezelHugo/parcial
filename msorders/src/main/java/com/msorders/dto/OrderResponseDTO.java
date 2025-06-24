package com.msorders.dto;

import java.time.LocalDateTime;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponseDTO {
  private Long id;
  private String customer;
  private LocalDateTime date;
  private String status;
  private Double total;
  private List<OrderItemResponseDTO> items;
}