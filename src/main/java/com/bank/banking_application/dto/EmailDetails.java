package com.bank.banking_application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailDetails {

    @Schema(name = "Recipient Email Address")
    private String recipient;

    @Schema(name = "Email Message Body")
    private String messageBody;

    @Schema(name = "Email Subject")
    private String subject;
}
