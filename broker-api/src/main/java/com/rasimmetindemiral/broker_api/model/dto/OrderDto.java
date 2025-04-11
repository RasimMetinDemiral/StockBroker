package com.rasimmetindemiral.broker_api.model.dto;

import com.rasimmetindemiral.broker_api.model.enums.OrderSide;
import com.rasimmetindemiral.broker_api.model.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class OrderDto {
    private Long id;
    private Long customerId;
    private String assetName;
    private OrderSide orderSide;
    private Double size;
    private Double price;
    private OrderStatus orderStatus;
    private LocalDateTime createDate;
}
