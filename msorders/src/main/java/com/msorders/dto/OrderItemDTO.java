package com.msorders.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class OrderItemDTO {
  @NotNull(message = "Debe especificar el ID del producto")
  @Min(value = 1, message = "Ha ingresado un producto no valido")
  private Long productId;

  @NotNull(message = "La cantidad es obligatoria")
  @Min(value = 1, message = "La cantidad debe ser al menos 1")
  private Integer quantity;
}
