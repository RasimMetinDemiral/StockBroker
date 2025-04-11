package com.rasimmetindemiral.broker_api.service;

import com.rasimmetindemiral.broker_api.model.entity.Customer;
import com.rasimmetindemiral.broker_api.respository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService{
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Optional<Customer> findById(Long id) {
        logger.info("Finding customer by id: {}", id);
        return customerRepository.findById(id);
    }

    @Override
    public Optional<Customer> findByUsername(String username) {
        logger.info("find customer by username: {}", username);
        return customerRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        logger.info("Check customer exists by username: {}", username);
        return customerRepository.existsByUsername(username);
    }

    @Override
    public Customer save(Customer customer) {
        logger.info("save customer: {}", customer);
        return customerRepository.save(customer);
    }
    @Override
    public List<Customer> findAll() {
        logger.info("Fetching all customers");
        return customerRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        customerRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return customerRepository.existsById(id);
    }
}
