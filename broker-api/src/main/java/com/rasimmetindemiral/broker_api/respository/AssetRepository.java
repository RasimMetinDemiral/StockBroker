package com.rasimmetindemiral.broker_api.respository;

import com.rasimmetindemiral.broker_api.model.entity.Asset;
import com.rasimmetindemiral.broker_api.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByCustomer(Customer customer);
    Optional<Asset> findByCustomerAndAssetName(Customer customer,String assetName);
}
