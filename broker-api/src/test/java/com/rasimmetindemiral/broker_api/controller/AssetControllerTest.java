package com.rasimmetindemiral.broker_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rasimmetindemiral.broker_api.model.entity.Asset;
import com.rasimmetindemiral.broker_api.model.entity.Customer;
import com.rasimmetindemiral.broker_api.model.enums.Role;
import com.rasimmetindemiral.broker_api.service.AssetService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(AssetControllerTest.MockConfig.class)
class AssetControllerTest {

    @TestConfiguration
    static class MockConfig {
        @Bean
        public AssetService assetService() {
            return Mockito.mock(AssetService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AssetService assetService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "metin", roles = {"ADMIN"})
    void getAssetsByCustomerId_shouldReturnAssets() throws Exception {
        Asset asset = Asset.builder().id(1L).size(100.0).usableSize(80.0).customer(Customer.builder().id(1L).username("metin").role(Role.ADMIN).build()).build();
        given(assetService.getAssetsByCustomerId(1L)).willReturn(Collections.singletonList(asset));
        mockMvc.perform(get("/assets/admin/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteAsset_shouldReturnSuccessMessage() throws Exception {
        Long assetId = 1L;
        given(assetService.existsById(assetId)).willReturn(true);
        willDoNothing().given(assetService).deleteById(assetId);
        mockMvc.perform(delete("/assets/{id}", assetId)).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("asset deleted successfully"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteAsset_shouldReturnNotFound() throws Exception {
        Long assetId = 99L;
        given(assetService.existsById(assetId)).willReturn(false);
        mockMvc.perform(delete("/assets/{id}", assetId)).andExpect(status().isNotFound()).andExpect(jsonPath("$.error").value("asset not found with id: " + assetId));
    }
}
