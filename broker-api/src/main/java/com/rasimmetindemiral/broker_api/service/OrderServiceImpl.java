package com.rasimmetindemiral.broker_api.service;

import com.rasimmetindemiral.broker_api.model.entity.Asset;
import com.rasimmetindemiral.broker_api.model.entity.Customer;
import com.rasimmetindemiral.broker_api.model.entity.Order;
import com.rasimmetindemiral.broker_api.model.enums.OrderSide;
import com.rasimmetindemiral.broker_api.model.enums.OrderStatus;
import com.rasimmetindemiral.broker_api.respository.AssetRepository;
import com.rasimmetindemiral.broker_api.respository.CustomerRepository;
import com.rasimmetindemiral.broker_api.respository.OrderRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    public final OrderRepository orderRepository;
    public final CustomerRepository customerRepository;
    public final AssetRepository assetRepository;

    public OrderServiceImpl(OrderRepository orderRepository, CustomerRepository customerRepository, AssetRepository assetRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.assetRepository = assetRepository;
    }


    @Override
    public Order createOrder(Order order) {
        logger.info("Create new order: {}", order);

        Customer customer = order.getCustomer();
        if (customer == null || customer.getId() == 0) {
            throw new RuntimeException("Customer info is missing or invalid");
        }

        Asset tryAsset = assetRepository.findByCustomerAndAssetName(customer, "TRY").orElseThrow(() -> new RuntimeException("TRY asset not found for customer"));

        if (order.getOrderSide() == OrderSide.BUY) {
            double requiredAmount = order.getSize() * order.getPrice();
            if (tryAsset.getUsableSize() < requiredAmount) {
                throw new RuntimeException("Insufficient TRY balance for BUY order");
            }
            tryAsset.setUsableSize(tryAsset.getUsableSize() - requiredAmount);
        } else if (order.getOrderSide() == OrderSide.SELL) {
            Asset asset = assetRepository.findByCustomerAndAssetName(customer, order.getAssetName()).orElseThrow(() -> new RuntimeException("Asset not found for SELL order"));
            if (asset.getUsableSize() < order.getSize()) {
                throw new RuntimeException("Insufficient asset balance for SELL order");
            }
            asset.setUsableSize(asset.getUsableSize() - order.getSize());
            assetRepository.save(asset);
        }

        assetRepository.save(tryAsset);

        order.setOrderStatus(OrderStatus.PENDING);
        order.setCreateDate(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void matchOrders() {
        logger.info("Matching orders starts...");
        List<String> assetNames = orderRepository.findAll().stream()
                .map(Order::getAssetName)
                .map(this::toCamelCase)
                .distinct()
                .collect(Collectors.toList());

        for (String assetName : assetNames) {
            List<Order> buyOrders = orderRepository.findByAssetNameAndOrderSideAndOrderStatusOrderByCreateDateAsc(assetName, OrderSide.BUY, OrderStatus.PENDING);
            List<Order> sellOrders = orderRepository.findByAssetNameAndOrderSideAndOrderStatusOrderByCreateDateAsc(assetName, OrderSide.SELL, OrderStatus.PENDING);

            for (Order buy : buyOrders) {
                for (Order sell : sellOrders) {
                    if (buy.getPrice() >= sell.getPrice() && buy.getSize().equals(sell.getSize())) {
                        logger.info("Matching BUY order {} with SELL order {}", buy.getId(), sell.getId());

                        // order status guncellenir
                        buy.setOrderStatus(OrderStatus.MATCHED);
                        sell.setOrderStatus(OrderStatus.MATCHED);

                        // customer assests guncellenir
                        Asset buyerAsset = assetRepository.findByCustomerAndAssetName(buy.getCustomer(), buy.getAssetName()).orElse(new Asset());
                        buyerAsset.setAssetName(buy.getAssetName());
                        buyerAsset.setCustomer(buy.getCustomer());
                        buyerAsset.setSize(buyerAsset.getSize() + buy.getSize());
                        buyerAsset.setUsableSize(buyerAsset.getUsableSize() + buy.getSize());
                        assetRepository.save(buyerAsset);

                        // Saticinin TRY bakiyesi arttirilir
                        double transactionTotal = sell.getSize() * sell.getPrice();
                        Asset sellerTryAsset = assetRepository.findByCustomerAndAssetName(sell.getCustomer(), "TRY").orElse(new Asset("TRY", sell.getCustomer(), 0.0, 0.0));
                        sellerTryAsset.setSize(sellerTryAsset.getSize() + transactionTotal);
                        sellerTryAsset.setUsableSize(sellerTryAsset.getUsableSize() + transactionTotal);
                        assetRepository.save(sellerTryAsset);

                        // order kaydetme
                        orderRepository.save(buy);
                        orderRepository.save(sell);
                        break;
                    }
                }
            }
        }
        logger.info("Order matching is completed.");
    }


    @Override
    public List<Order> getAllOrders() {
        logger.info("Fetching all orders");
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByCustomerId(Long customerId) {
        logger.info("fetch orders for customer id: {}", customerId);
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
        return orderRepository.findByCustomer(customer);
    }

    @Override
    public void cancelOrder(Long orderId) {
        logger.info("cancel order with id: {}", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Only PENDING orders can be canceled");
        }

        Customer customer = order.getCustomer();
        if (order.getOrderSide() == OrderSide.BUY) {
            double refundAmount = order.getSize() * order.getPrice();
            Asset tryAsset = assetRepository.findByCustomerAndAssetName(customer, "TRY").orElseThrow(() -> new RuntimeException("TRY asset not found for refund"));
            tryAsset.setUsableSize(tryAsset.getUsableSize() + refundAmount);
            assetRepository.save(tryAsset);
        } else if (order.getOrderSide() == OrderSide.SELL) {
            Asset asset = assetRepository.findByCustomerAndAssetName(customer, order.getAssetName()).orElseThrow(() -> new RuntimeException("Asset not found for refund"));
            asset.setUsableSize(asset.getUsableSize() + order.getSize());
            assetRepository.save(asset);
        }

        order.setOrderStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    @Override
    public List<Asset> getAssetsByCustomerId(Long customerId) {
        logger.info("fetch assets for customer id: {}", customerId);
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
        return assetRepository.findByCustomer(customer);
    }
    private String toCamelCase(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
    @Override
    public Order getOrderById(Long id) {
        logger.info("Fetching order by id: {}", id);
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }
}
