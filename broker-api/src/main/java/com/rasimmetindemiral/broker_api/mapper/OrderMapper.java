package com.rasimmetindemiral.broker_api.mapper;

import com.rasimmetindemiral.broker_api.model.dto.OrderDto;
import com.rasimmetindemiral.broker_api.model.entity.Customer;
import com.rasimmetindemiral.broker_api.model.entity.Order;

public class OrderMapper {
    public static OrderDto toDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .assetName(order.getAssetName())
                .orderSide(order.getOrderSide())
                .size(order.getSize())
                .price(order.getPrice())
                .orderStatus(order.getOrderStatus())
                .createDate(order.getCreateDate())
                .build();
    }

    public static Order toEntity(OrderDto dto, Customer customer) {
        return Order.builder()
                .id(dto.getId())
                .customer(customer)
                .assetName(dto.getAssetName())
                .orderSide(dto.getOrderSide())
                .size(dto.getSize())
                .price(dto.getPrice())
                .orderStatus(dto.getOrderStatus())
                .createDate(dto.getCreateDate())
                .build();
    }
}