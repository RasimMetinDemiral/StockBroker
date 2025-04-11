package com.rasimmetindemiral.broker_api.model.entity;

import com.rasimmetindemiral.broker_api.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String password;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Asset> assets;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Order> orders;

    @Enumerated(EnumType.STRING)
    private Role role;
}
