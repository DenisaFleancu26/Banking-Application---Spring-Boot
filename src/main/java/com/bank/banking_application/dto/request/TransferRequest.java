package com.bank.banking_application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    @Schema(name = "Source Account Number")
    @NotBlank(message = "Source account number must not be blank")
    private String sourceAccountNumber;

    @Schema(name = "Destination Account Number")
    @NotBlank(message = "Destination account number must not be blank")
    private String destinationAccountNumber;

    @Schema(name = "Transfer Amount")
    @DecimalMin(value = "0", inclusive = false, message = "Transfer amount must be greater than 0 ")
    private BigDecimal amount;
}
