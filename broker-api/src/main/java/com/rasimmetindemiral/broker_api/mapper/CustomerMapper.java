package com.rasimmetindemiral.broker_api.mapper;

import com.rasimmetindemiral.broker_api.model.dto.CustomerDto;
import com.rasimmetindemiral.broker_api.model.entity.Customer;


public class CustomerMapper {
    public static CustomerDto toDto(Customer customer) {
        return CustomerDto.builder()
                .id(customer.getId())
                .username(customer.getUsername())
                .password(customer.getPassword()) // sadece dto dan alirken kullaniyorum maskeli sekilde
                .role(customer.getRole())
                .build();
    }

    public static Customer toEntity(CustomerDto dto) {
        return Customer.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .role(dto.getRole())
                .build();
    }
}