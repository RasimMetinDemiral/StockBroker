package com.rasimmetindemiral.broker_api.service;

import com.rasimmetindemiral.broker_api.model.entity.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Optional<Customer> findById(Long id);
    Optional<Customer> findByUsername(String username);
    boolean existsByUsername(String username);
    Customer save(Customer customer);
    List<Customer> findAll();
}
