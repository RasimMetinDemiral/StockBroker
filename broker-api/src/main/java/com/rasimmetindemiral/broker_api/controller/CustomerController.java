package com.rasimmetindemiral.broker_api.controller;

import com.rasimmetindemiral.broker_api.mapper.CustomerMapper;
import com.rasimmetindemiral.broker_api.model.dto.CustomerDto;
import com.rasimmetindemiral.broker_api.model.entity.Customer;
import com.rasimmetindemiral.broker_api.model.enums.Role;
import com.rasimmetindemiral.broker_api.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;

    // Mevcut user -- getCurrentCustomer
    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerDto> getCurrentCustomer(@AuthenticationPrincipal Customer customer) {
        return ResponseEntity.ok(CustomerMapper.toDto(customer));
    }

    // admin, ID ile customer data list
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Long id) {
        return customerService.findById(id).map(CustomerMapper::toDto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // admin, ID ile all customer data list
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        List<CustomerDto> customers = customerService.findAll().stream().map(CustomerMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(customers);
    }

    // new customer reqister service
    @PostMapping("/register")
    public ResponseEntity<CustomerDto> register(@RequestBody CustomerDto dto) {
        if (customerService.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        String rawPassword = dto.getPassword();
        if (rawPassword == null || rawPassword.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }

        Customer newCustomer = Customer.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.CUSTOMER)
                .build();

        Customer saved = customerService.save(newCustomer);
        CustomerDto responseDto = CustomerMapper.toDto(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
