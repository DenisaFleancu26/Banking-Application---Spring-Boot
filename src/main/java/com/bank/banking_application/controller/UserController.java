package com.bank.banking_application.controller;

import com.bank.banking_application.dto.request.*;
import com.bank.banking_application.dto.response.BankResponse;
import com.bank.banking_application.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;

@RestController
@RequestMapping("/api/user/account")
@Tag(name = "User Account APIs")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            description = "Retrieves the current balance for the account specified by the account number."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Balance retrieved successfully."
    )
    @PostMapping("/balanceEnquiry")
    public BankResponse balanceEnquiry(@Valid @RequestBody EnquiryRequest request) throws AccountNotFoundException {
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
    public BankResponse creditAccount (@Valid @RequestBody CreditDebitRequest request) throws AccountNotFoundException {
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
    public BankResponse debitAccount (@Valid @RequestBody CreditDebitRequest request) throws AccountNotFoundException {
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
    public BankResponse transfer(@Valid @RequestBody TransferRequest request) throws AccountNotFoundException {
        return  userService.transfer(request);
    }

}
