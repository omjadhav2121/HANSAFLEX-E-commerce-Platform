package com.hansaflex.ecommerce.controller;

import com.hansaflex.ecommerce.dto.ApiResponse;
import com.hansaflex.ecommerce.dto.LoginRequest;
import com.hansaflex.ecommerce.dto.LoginResponse;
import com.hansaflex.ecommerce.dto.RegisterRequest;
import com.hansaflex.ecommerce.entity.User;
import com.hansaflex.ecommerce.security.JwtUtil;
import com.hansaflex.ecommerce.service.UserDetailsServiceImpl;
import com.hansaflex.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = (User) userDetails;
            
            // Generate JWT with region and customerId claims
            String token = jwtUtil.generateToken(userDetails, user.getRegion(), user.getId().toString());
            
            LoginResponse loginResponse = LoginResponse.builder()
                    .token(token)
                    .username(user.getUsername())
                    .role(user.getRole())
                    .region(user.getRegion())
                    .message("Login successful")
                    .build();
            
            log.info("Login successful for user: {}", user.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));
            
        } catch (Exception e) {
            log.error("Login failed for user: {}, error: {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid username or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Registration attempt for user: {}", registerRequest.getUsername());
        
        try {
            User user = userService.registerUser(registerRequest);
            
            // Generate JWT with region and customerId claims
            String token = jwtUtil.generateToken(user, user.getRegion(), user.getId().toString());
            
            LoginResponse loginResponse = LoginResponse.builder()
                    .token(token)
                    .username(user.getUsername())
                    .role(user.getRole())
                    .region(user.getRegion())
                    .message("Registration successful")
                    .build();
            
            log.info("Registration successful for user: {}", user.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Registration successful", loginResponse));
            
        } catch (Exception e) {
            log.error("Registration failed for user: {}, error: {}", registerRequest.getUsername(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<String>> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7); // Remove "Bearer " prefix
            if (jwtUtil.validateToken(jwt)) {
                return ResponseEntity.ok(ApiResponse.success("Token is valid"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("Token is invalid"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Token validation failed"));
        }
    }
}
