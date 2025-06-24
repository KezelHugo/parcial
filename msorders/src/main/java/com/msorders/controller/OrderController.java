package com.msorders.controller;

import com.msorders.dto.OrderDTO;
import com.msorders.dto.OrderResponseDTO;
import com.msorders.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

  private static final String TIMESTAMP = "timestamp";
  private static final String STATUS = "status";
  private static final String ERROR = "error";
  private static final String MESSAGE = "message";

  private final OrderService orderService;

  @PostMapping("")
  public ResponseEntity<OrderResponseDTO> create(@Valid @RequestBody OrderDTO dto) {
    OrderResponseDTO response = orderService.create(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("")
  public ResponseEntity<List<OrderResponseDTO>> listAll() {
    List<OrderResponseDTO> orders = orderService.listAll();
    return ResponseEntity.ok(orders);
  }

  @GetMapping("/search")
  public ResponseEntity<Map<String, Object>> searchByCustomer(@RequestParam String customer) {
    Map<String, Object> response = new HashMap<>();

    if (customer == null || customer.trim().isEmpty()) {
      response.put(ERROR, "Parámetro inválido");
      response.put(MESSAGE, "El parámetro 'cliente' no puede estar vacío");
      response.put(TIMESTAMP, LocalDateTime.now());
      response.put(STATUS, HttpStatus.BAD_REQUEST.value());
      return ResponseEntity.badRequest().body(response);
    }

    List<OrderResponseDTO> results = orderService.findByCustomer(customer);

    if (results.isEmpty()) {
      response.put(MESSAGE, "No se encontraron órdenes para el cliente: " + customer);
      response.put("orders", Collections.emptyList());
      response.put(TIMESTAMP, LocalDateTime.now());
      response.put(STATUS, HttpStatus.NOT_FOUND.value());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    response.put("orders", results);
    response.put("count", results.size());
    response.put(TIMESTAMP, LocalDateTime.now());
    response.put(STATUS, HttpStatus.OK.value());
    return ResponseEntity.ok(response);
  }

}