package com.rasimmetindemiral.broker_api.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssetDto {
    private Long id;
    private String assetName;
    private Double size;
    private Double usableSize;
    private Long customerId;
}
