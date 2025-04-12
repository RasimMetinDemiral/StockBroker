package com.rasimmetindemiral.broker_api.service;

import com.rasimmetindemiral.broker_api.model.entity.Asset;
import com.rasimmetindemiral.broker_api.model.entity.Customer;
import com.rasimmetindemiral.broker_api.respository.AssetRepository;
import com.rasimmetindemiral.broker_api.respository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AssetServiceImplTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AssetServiceImpl assetService;

    private Customer customer;
    private Asset asset;

    @BeforeEach
    void setup() {
        customer = Customer.builder().id(1L).username("john").build();
        asset = Asset.builder().id(10L).assetName("BTC").size(10.0).usableSize(5.0).customer(customer).build();
    }

    @Test
    void testGetAssetsByCustomerId_shouldReturnAssets() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(assetRepository.findByCustomer(customer)).thenReturn(Arrays.asList(asset));
        List<Asset> assets = assetService.getAssetsByCustomerId(1L);
        assertNotNull(assets);
        assertEquals(1, assets.size());
        assertEquals("BTC", assets.get(0).getAssetName());
    }

    @Test
    void testUpdateAsset_shouldReturnUpdatedAsset() {
        when(assetRepository.save(asset)).thenReturn(asset);
        Asset updated = assetService.updateAsset(asset);
        assertNotNull(updated);
        assertEquals("BTC", updated.getAssetName());
    }

    @Test
    void testGetAssetById_shouldReturnAsset() {
        when(assetRepository.findById(10L)).thenReturn(Optional.of(asset));
        Asset found = assetService.getAssetById(10L);
        assertNotNull(found);
        assertEquals("BTC", found.getAssetName());
        assertEquals(10.0, found.getSize());
    }

    @Test
    void testGetAssetById_shouldThrowIfNotFound() {
        when(assetRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> assetService.getAssetById(99L));
        assertEquals("Asset not found with id: 99", ex.getMessage());
    }
}
