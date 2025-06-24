package com.msorders.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class OrderDTO {
  @NotBlank(message = "El nombre del cliente es obligatorio")
  private String customer;

  @NotEmpty(message = "La orden debe contener al menos un producto")
  @Valid
  private List<OrderItemDTO> items;
}