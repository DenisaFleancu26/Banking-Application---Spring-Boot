package com.bank.banking_application.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TransactionDTO {

    private String transactionType;
    private BigDecimal amount;
    private String accountNumber;
}
