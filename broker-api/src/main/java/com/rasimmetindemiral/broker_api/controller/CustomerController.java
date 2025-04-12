package com.rasimmetindemiral.broker_api.controller;

import com.rasimmetindemiral.broker_api.mapper.CustomerMapper;
import com.rasimmetindemiral.broker_api.model.dto.CustomerDto;
import com.rasimmetindemiral.broker_api.model.entity.Customer;
import com.rasimmetindemiral.broker_api.model.enums.Role;
import com.rasimmetindemiral.broker_api.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;

    // Mevcut user -- getCurrentCustomer
    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerDto> getCurrentCustomer(@AuthenticationPrincipal Customer customer) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // get username from authentication.
        Optional<Customer> optionalCustomer = customerService.findByUsername(username);

        if (optionalCustomer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        CustomerDto dto = CustomerMapper.toDto(optionalCustomer.get());
        return ResponseEntity.ok(dto);
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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<Map<String, String>> deleteCustomer(@PathVariable Long id) {
        if (!customerService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Customer not found with id: " + id));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        logger.info("currentUsername {}",currentUsername);

        Customer targetCustomer = customerService.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));

        //kendi disinda farkli customer silinmemesi icin kontrol ekledim.
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER")) && !targetCustomer.getUsername().equals(currentUsername)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "You are not allowed to delete another customer."));
        }

        customerService.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Customer deleted successfully"));
    }
}
