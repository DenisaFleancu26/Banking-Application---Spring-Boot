package com.bank.banking_application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditDebitRequest {

    @Schema( name = "Account Number")
    private String accountNumber;

    @Schema(name = "Amount to Credit/Debit")
    private BigDecimal amount;
}
