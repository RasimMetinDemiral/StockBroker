package com.rasimmetindemiral.broker_api.service;

import com.rasimmetindemiral.broker_api.model.entity.Asset;
import java.util.List;

public interface AssetService {
    List<Asset> getAssetsByCustomerId(Long customerId);
    Asset updateAsset(Asset asset);
    Asset getAssetById(Long id);
    void deleteById(Long id);
    boolean existsById(Long id);
}
