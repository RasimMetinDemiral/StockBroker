package com.rasimmetindemiral.broker_api.service;

import com.rasimmetindemiral.broker_api.model.entity.Asset;
import com.rasimmetindemiral.broker_api.model.entity.Customer;
import com.rasimmetindemiral.broker_api.respository.AssetRepository;
import com.rasimmetindemiral.broker_api.respository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetServiceImpl implements AssetService{
    private static final Logger logger = LoggerFactory.getLogger(AssetServiceImpl.class);


    private final AssetRepository assetRepository;
    private final CustomerRepository customerRepository;

    public AssetServiceImpl(AssetRepository assetRepository, CustomerRepository customerRepository) {
        this.assetRepository = assetRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Asset> getAssetsByCustomerId(Long customerId) {
        logger.info("get assets for customer id: {}", customerId);
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        return assetRepository.findByCustomer(customer);
    }

    @Override
    public Asset updateAsset(Asset asset) {
        logger.info("Update asset: {}", asset.getAssetName());
        return assetRepository.save(asset);
    }

    @Override
    public Asset getAssetById(Long id) {
        logger.info("get asset by id: {}", id);
        return assetRepository.findById(id).orElseThrow(()-> new RuntimeException("Asset not found with id: " + id));
    }

    @Override
    public void deleteById(Long id) {
        assetRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return assetRepository.existsById(id);
    }
}
