package com.bank.banking_application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
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

    @Schema(name = "Email Attachment")
    private String attachment;
}
