package com.rasimmetindemiral.broker_api.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rasimmetindemiral.broker_api.model.enums.Role;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CustomerDto {
    private Long id;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // sifreyi sadece deserilization icin, json response body de donmez
    private String password;
    private Role role;
}
