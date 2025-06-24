package com.msorders.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long productId;
  private String name;
  private Integer quantity;
  private Double price;

  @ManyToOne
  @JoinColumn(name = "order_id")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JsonIgnore
  private Order order;
}
