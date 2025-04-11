package com.rasimmetindemiral.broker_api.config;

import com.rasimmetindemiral.broker_api.model.entity.Asset;
import com.rasimmetindemiral.broker_api.model.entity.Customer;
import com.rasimmetindemiral.broker_api.model.entity.Order;
import com.rasimmetindemiral.broker_api.model.enums.OrderSide;
import com.rasimmetindemiral.broker_api.model.enums.OrderStatus;
import com.rasimmetindemiral.broker_api.model.enums.Role;
import com.rasimmetindemiral.broker_api.respository.AssetRepository;
import com.rasimmetindemiral.broker_api.respository.CustomerRepository;
import com.rasimmetindemiral.broker_api.respository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final CustomerRepository customerRepository;
    private final AssetRepository assetRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(CustomerRepository customerRepository, AssetRepository assetRepository, OrderRepository orderRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.assetRepository = assetRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (customerRepository.count() == 0) {
            logger.info("run() method is started. Initial data is generating...");

            // default admin values
            Customer admin = Customer.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            customerRepository.save(admin);

            // default customer values
            Customer customer = Customer.builder()
                    .username("Metin")
                    .password(passwordEncoder.encode("123456"))
                    .role(Role.CUSTOMER)
                    .build();
            customerRepository.save(customer);

            // default customer icin initial asset value
            Asset tryAsset = new Asset("TRY", customer, 10000.0, 10000.0);
            assetRepository.save(tryAsset);

            // initial btc asset value olusturulmasi
            Asset btcAsset = new Asset("BTC", customer, 2.0, 2.0);
            assetRepository.save(btcAsset);

            // BUY order
            Order buyOrder = Order.builder()
                    .assetName("BTC")
                    .orderSide(OrderSide.BUY)
                    .orderStatus(OrderStatus.PENDING)
                    .size(1.0)
                    .price(500.0)
                    .createDate(LocalDateTime.now())
                    .customer(customer)
                    .build();
            orderRepository.save(buyOrder);

            // SELL order
            Order sellOrder = Order.builder()
                    .assetName("BTC")
                    .orderSide(OrderSide.SELL)
                    .orderStatus(OrderStatus.PENDING)
                    .size(1.0)
                    .price(500.0)
                    .createDate(LocalDateTime.now())
                    .customer(customer)
                    .build();
            orderRepository.save(sellOrder);

            logger.info("default data is generated...");
        }
    }
}