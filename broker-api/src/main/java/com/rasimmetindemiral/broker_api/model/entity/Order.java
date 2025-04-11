package com.rasimmetindemiral.broker_api.model.entity;

import com.rasimmetindemiral.broker_api.model.enums.OrderSide;
import com.rasimmetindemiral.broker_api.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double price;
    private Double size;
    private String assetName;

    @Enumerated(EnumType.STRING)
    private OrderSide orderSide;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private LocalDateTime createDate;
    @ManyToOne
    private Customer customer;
}
