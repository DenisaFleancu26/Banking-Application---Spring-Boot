package com.bank.banking_application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnquiryRequest {

    @Schema(name = "Account Number")
    @NotBlank(message = "Account number is required")
    private String accountNumber;
}
