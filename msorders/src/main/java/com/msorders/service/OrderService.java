package com.msorders.service;

import com.msorders.client.ProductClient;
import com.msorders.client.ProductClient.ProductInfo;
import com.msorders.dto.OrderDTO;
import com.msorders.dto.OrderItemDTO;
import com.msorders.dto.OrderItemResponseDTO;
import com.msorders.dto.OrderResponseDTO;
import com.msorders.exception.InsufficientStockException;
import com.msorders.model.Order;
import com.msorders.model.OrderItem;
import com.msorders.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor

public class OrderService {

  private final OrderRepository orderRepository;
  private final ProductClient productClient;

  public OrderResponseDTO create(OrderDTO dto) {
    List<OrderItem> orderItems = new ArrayList<>();
    double total = 0.0;

    for (OrderItemDTO itemDTO : dto.getItems()) {
      ProductInfo product = productClient.validateProduct(itemDTO.getProductId(), itemDTO.getQuantity());

      if (!product.isStockAvailable()) {
        throw new InsufficientStockException("Stock insuficiente para el producto: " + product.getName());
      }

      double subtotal = product.getPrice() * itemDTO.getQuantity();
      total += subtotal;

      OrderItem item = OrderItem.builder()
        .productId(product.getId())
        .name(product.getName())
        .price(product.getPrice())
        .quantity(itemDTO.getQuantity())
        .build();

      orderItems.add(item);
    }

    for (OrderItem item : orderItems) {
      productClient.decreaseStock(item.getProductId(), item.getQuantity());
    }

    Order order = Order.builder()
      .customer(dto.getCustomer())
      .date(LocalDateTime.now())
      .status("CREATED")
      .total(total)
      .items(orderItems)
      .build();

    orderItems.forEach(item -> item.setOrder(order));

    Order saved = orderRepository.save(order);

    // Convertimos a OrderResponseDTO
    List<OrderItemResponseDTO> itemResponses = saved.getItems().stream()
      .map(item -> OrderItemResponseDTO.builder()
        .productId(item.getProductId())
        .name(item.getName())
        .price(item.getPrice())
        .quantity(item.getQuantity())
        .build())
      .toList();

    return OrderResponseDTO.builder()
      .id(saved.getId())
      .customer(saved.getCustomer())
      .date(saved.getDate())
      .status(saved.getStatus())
      .total(saved.getTotal())
      .items(itemResponses)
      .build();
  }

  public List<OrderResponseDTO> listAll() {
    return orderRepository.findAll().stream()
      .map(order -> {
        List<OrderItemResponseDTO> items = order.getItems().stream()
          .map(item -> OrderItemResponseDTO.builder()
            .productId(item.getProductId())
            .name(item.getName())
            .price(item.getPrice())
            .quantity(item.getQuantity())
            .build())
          .toList();

        return OrderResponseDTO.builder()
          .id(order.getId())
          .customer(order.getCustomer())
          .date(order.getDate())
          .status(order.getStatus())
          .total(order.getTotal())
          .items(items)
          .build();
      })
      .toList();
  }

  public List<OrderResponseDTO> findByCustomer(String customer) {
    return orderRepository.findByCustomerContainingIgnoreCase(customer).stream()
      .map(order -> {
        List<OrderItemResponseDTO> items = order.getItems().stream()
          .map(item -> OrderItemResponseDTO.builder()
            .productId(item.getProductId())
            .name(item.getName())
            .price(item.getPrice())
            .quantity(item.getQuantity())
            .build())
          .toList();

        return OrderResponseDTO.builder()
          .id(order.getId())
          .customer(order.getCustomer())
          .date(order.getDate())
          .status(order.getStatus())
          .total(order.getTotal())
          .items(items)
          .build();
      })
      .toList();
  }

}
