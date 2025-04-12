package com.rasimmetindemiral.broker_api.controller;

import com.rasimmetindemiral.broker_api.model.dto.CustomerDto;
import com.rasimmetindemiral.broker_api.model.entity.Customer;
import com.rasimmetindemiral.broker_api.model.enums.Role;
import com.rasimmetindemiral.broker_api.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CustomerController customerController;

    public CustomerControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCustomerByIdFound() {
        Customer customer = Customer.builder().id(1L).username("user").role(Role.CUSTOMER).build();
        when(customerService.findById(1L)).thenReturn(Optional.of(customer));
        ResponseEntity<CustomerDto> response = customerController.getCustomerById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("user", response.getBody().getUsername());
    }

    @Test
    void testGetCustomerByIdNotFound() {
        when(customerService.findById(99L)).thenReturn(Optional.empty());
        ResponseEntity<CustomerDto> response = customerController.getCustomerById(99L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteCustomerNotFound() {
        when(customerService.existsById(99L)).thenReturn(false);
        ResponseEntity<?> response = customerController.deleteCustomer(99L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testFindAllCustomers() {
        List<Customer> customers = List.of(Customer.builder().username("a").build(), Customer.builder().username("b").build());
        when(customerService.findAll()).thenReturn(customers);
        ResponseEntity<List<CustomerDto>> response = customerController.getAllCustomers();
        assertEquals(2, response.getBody().size());
    }
}