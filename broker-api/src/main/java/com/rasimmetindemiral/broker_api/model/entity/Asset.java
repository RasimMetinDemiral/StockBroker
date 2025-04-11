package com.rasimmetindemiral.broker_api.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String assetName;
    private Double size;
    private Double usableSize;

    @ManyToOne
    private Customer customer;

    public Asset(String assetName, Customer customer, Double size, Double usableSize) {
        this.assetName = assetName;
        this.customer = customer;
        this.size = size;
        this.usableSize = usableSize;
    }
}
