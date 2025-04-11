package com.rasimmetindemiral.broker_api.respository;

import com.rasimmetindemiral.broker_api.model.entity.Customer;
import com.rasimmetindemiral.broker_api.model.entity.Order;
import com.rasimmetindemiral.broker_api.model.enums.OrderSide;
import com.rasimmetindemiral.broker_api.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomer(Customer customer);
    List<Order> findByAssetNameAndOrderSideAndOrderStatusOrderByCreateDateAsc(String assetName, OrderSide orderSide, OrderStatus orderStatus);
}
