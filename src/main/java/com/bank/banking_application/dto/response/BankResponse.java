package com.bank.banking_application.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankResponse {

    @Schema(name = "Response Code")
    private HttpStatus responseCode;

    @Schema(name = "Response Message")
    private String responseMessage;

    @Schema(name = "Account Information")
    private AccountInfo accountInfo;
}
