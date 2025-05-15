package com.bank.banking_application.controller;

import com.bank.banking_application.dto.*;
import com.bank.banking_application.service.AuthService;
import com.bank.banking_application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Account Management APIs")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    @Operation(
            summary = "Create New User Account",
            description = "Creates a new bank user account and generates a unique account number."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Account successfully created."
    )
    @PostMapping("/register")
    public BankResponse createAccount(@RequestBody UserRequest userRequest){
        return authService.register(userRequest);
    }

    @PostMapping("/login")
    public BankResponse login(@RequestBody LoginDto loginDto){
        return authService.login(loginDto);
    }


    @Operation(
            summary = "Balance Enquiry",
            description = "Retrieves the current balance for the account specified by the account number."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Balance retrieved successfully."
    )
    @PostMapping("/balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request){
        return userService.balanceEnquiry(request);
    }

    @Operation(
            summary = "Credit Account",
            description = "Adds the specified amount to the given account number."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Account successfully credited."
    )
    @PostMapping("/credit")
    public BankResponse creditAccount (@RequestBody CreditDebitRequest request){
        return userService.creditAccount(request);
    }

    @Operation(
            summary = "Debit Account",
            description = "Deducts the specified amount from the given account number if sufficient balance is available."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Account successfully debited."
    )
    @PostMapping("/debit")
    public BankResponse debitAccount (@RequestBody CreditDebitRequest request){
        return userService.debitAccount(request);
    }

    @Operation(
            summary = "Transfer Funds",
            description = "Transfers a specified amount from a source account to a destination account."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Transfer completed successfully."
    )
    @PostMapping("/transfer")
    public BankResponse transfer(@RequestBody TransferRequest request){
        return  userService.transfer(request);
    }

}
