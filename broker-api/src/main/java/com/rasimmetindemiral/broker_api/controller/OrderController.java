package com.rasimmetindemiral.broker_api.controller;

import com.rasimmetindemiral.broker_api.mapper.OrderMapper;
import com.rasimmetindemiral.broker_api.model.dto.OrderDto;
import com.rasimmetindemiral.broker_api.model.entity.Customer;
import com.rasimmetindemiral.broker_api.model.entity.Order;
import com.rasimmetindemiral.broker_api.model.enums.Role;
import com.rasimmetindemiral.broker_api.security.CustomUserDetails;
import com.rasimmetindemiral.broker_api.service.CustomerService;
import com.rasimmetindemiral.broker_api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CustomerService customerService;

    // create Order servisi
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto dto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Customer customer = userDetails.getCustomer();
        Order order = OrderMapper.toEntity(dto, customer);
        return ResponseEntity.ok(OrderMapper.toDto(orderService.createOrder(order)));
    }

    // get order list -- admin gets all data, customer get own data
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<List<OrderDto>> getOrders(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<OrderDto> orders;
        if (userDetails.getCustomer().getRole() == Role.ADMIN) {
            orders = orderService.getAllOrders().stream().map(OrderMapper::toDto).collect(Collectors.toList());
        } else {
            Long customerId = userDetails.getCustomer().getId();
            orders =orderService.getOrdersByCustomerId(customerId).stream().map(OrderMapper::toDto).collect(Collectors.toList());
        }
        return ResponseEntity.ok(orders);
    }

    //getOrderById
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Order order = orderService.getOrderById(id);
        if (userDetails.getCustomer().getRole() == Role.CUSTOMER && order.getCustomer().getId() != userDetails.getCustomer().getId()) {
            throw new AccessDeniedException("You are not authorized to access this order.");
        }
        return ResponseEntity.ok(OrderMapper.toDto(order));
    }

    // cancelOrder -- CUSTOMER only cancel own PENDING order
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Order order = orderService.getOrderById(id);
        if (order.getCustomer().getId() != userDetails.getCustomer().getId()) {
            throw new AccessDeniedException("You are not authorized to cancel this order.");
        }
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build(); //http 204 atilir
    }

    //all pending order match et
    @PostMapping("/match")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> matchPendingOrders() {
        orderService.matchOrders();
        return ResponseEntity.ok().build();
    }

}