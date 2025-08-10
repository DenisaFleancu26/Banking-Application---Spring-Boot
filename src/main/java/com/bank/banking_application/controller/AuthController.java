package com.bank.banking_application.controller;

import com.bank.banking_application.dto.request.*;
import com.bank.banking_application.dto.response.BankResponse;
import com.bank.banking_application.service.interfaces.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/auth")
@Tag(name = "User Account Management APIs")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Create New User Account",
            description = "Creates a new bank user account and generates a unique account number."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Account successfully created."
    )
    @PostMapping("/register")
    public BankResponse createAccount(@Valid @RequestBody UserRequest userRequest){
        return authService.register(userRequest);
    }

    @PostMapping("/login")
    public BankResponse login(@Valid @RequestBody LoginRequest loginRequest){
        return authService.login(loginRequest);
    }

}
