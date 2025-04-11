package com.rasimmetindemiral.broker_api.mapper;

import com.rasimmetindemiral.broker_api.model.dto.AssetDto;
import com.rasimmetindemiral.broker_api.model.entity.Asset;
import com.rasimmetindemiral.broker_api.model.entity.Customer;

public class AssetMapper {
    public static AssetDto toDto(Asset asset) {
        return AssetDto.builder()
                .id(asset.getId())
                .assetName(asset.getAssetName())
                .size(asset.getSize())
                .usableSize(asset.getUsableSize())
                .customerId(asset.getCustomer().getId())
                .build();
    }

    public static Asset toEntity(AssetDto dto, Customer customer) {
        return Asset.builder()
                .id(dto.getId())
                .assetName(dto.getAssetName())
                .size(dto.getSize())
                .usableSize(dto.getUsableSize())
                .customer(customer)
                .build();
    }
}
