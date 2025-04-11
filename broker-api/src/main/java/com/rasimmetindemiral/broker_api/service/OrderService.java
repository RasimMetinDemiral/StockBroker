package com.rasimmetindemiral.broker_api.service;

import com.rasimmetindemiral.broker_api.model.entity.Asset;
import com.rasimmetindemiral.broker_api.model.entity.Order;

import java.util.List;

public interface OrderService {
    Order createOrder(Order order);
    void matchOrders();
    List<Order> getAllOrders();
    List<Order> getOrdersByCustomerId(Long customerId);
    void cancelOrder(Long orderId);
    List<Asset> getAssetsByCustomerId(Long customerId);
    Order getOrderById(Long id);
}
