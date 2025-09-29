package com.hansaflex.ecommerce.dto;

import com.hansaflex.ecommerce.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private String username;
    private Role role;
    private String region;
    private String message;
}
