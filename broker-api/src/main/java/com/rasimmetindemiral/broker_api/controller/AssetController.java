package com.rasimmetindemiral.broker_api.controller;

import com.rasimmetindemiral.broker_api.mapper.AssetMapper;
import com.rasimmetindemiral.broker_api.model.dto.AssetDto;
import com.rasimmetindemiral.broker_api.model.entity.Asset;
import com.rasimmetindemiral.broker_api.model.entity.Customer;
import com.rasimmetindemiral.broker_api.model.enums.Role;
import com.rasimmetindemiral.broker_api.security.CustomUserDetails;
import com.rasimmetindemiral.broker_api.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    // customer assets list
    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<AssetDto>> getAssetsForCustomer(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long customerId = userDetails.getCustomer().getId();
        List<AssetDto> assetDtos = assetService.getAssetsByCustomerId(customerId).stream().map(AssetMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(assetDtos);
    }

    // Admin getAssetsByCustomerId
    @GetMapping("/admin/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AssetDto>> getAssetsByCustomerId(@PathVariable Long customerId) {
        List<AssetDto> assetDtos = assetService.getAssetsByCustomerId(customerId).stream().map(AssetMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(assetDtos);
    }


    // get Asset info ById
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<AssetDto> getAssetById(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Asset asset = assetService.getAssetById(id);
        if (userDetails.getCustomer().getRole() == Role.CUSTOMER && asset.getCustomer().getId() != userDetails.getCustomer().getId()) {
            throw new AccessDeniedException("You are not authorized to view this asset.");
        }
        return ResponseEntity.ok(AssetMapper.toDto(asset));
    }

    // update Asset
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<AssetDto> updateAsset(@PathVariable Long id, @RequestBody AssetDto dto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Asset existing = assetService.getAssetById(id);

        if (userDetails.getCustomer().getRole() == Role.CUSTOMER && existing.getCustomer().getId() != userDetails.getCustomer().getId()) {
            throw new AccessDeniedException("You are not authorized to update this asset.");
        }

        existing.setSize(dto.getSize());
        existing.setUsableSize(dto.getUsableSize());
        Asset updated = assetService.updateAsset(existing);
        return ResponseEntity.ok(AssetMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteAsset(@PathVariable Long id) {
        if (!assetService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "asset not found with id: " + id));
        }

        assetService.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "asset deleted successfully"));
    }
}
