package com.rasimmetindemiral.broker_api.service;

import com.rasimmetindemiral.broker_api.model.entity.Customer;
import com.rasimmetindemiral.broker_api.model.enums.Role;
import com.rasimmetindemiral.broker_api.respository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    private CustomerRepository customerRepository;
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        customerService = new CustomerServiceImpl(customerRepository);
    }

    @Test
    void testFindById() {
        Customer customer = Customer.builder().id(1L).username("metin").build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Optional<Customer> result = customerService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("metin", result.get().getUsername());
    }

    @Test
    void testExistsByUsername() {
        when(customerRepository.existsByUsername("user")).thenReturn(true);
        assertTrue(customerService.existsByUsername("user"));
    }

    @Test
    void testSave() {
        Customer customer = Customer.builder().username("new").role(Role.CUSTOMER).build();
        when(customerRepository.save(customer)).thenReturn(customer);
        Customer saved = customerService.save(customer);
        assertEquals("new", saved.getUsername());
    }

    @Test
    void testDeleteById() {
        customerService.deleteById(5L);
        verify(customerRepository).deleteById(5L);
    }

    @Test
    void testFindAll() {
        when(customerRepository.findAll()).thenReturn(List.of());
        assertNotNull(customerService.findAll());
    }

    @Test
    void testExistsById() {
        when(customerRepository.existsById(2L)).thenReturn(true);
        assertTrue(customerService.existsById(2L));
    }
}