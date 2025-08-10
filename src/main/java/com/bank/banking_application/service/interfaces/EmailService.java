package com.bank.banking_application.service.interfaces;

import com.bank.banking_application.dto.response.EmailDetails;
import org.springframework.scheduling.annotation.Async;

public interface EmailService {

    @Async
    void sendEmailAlert(EmailDetails emailDetails);
    @Async
    void sendEmailWithAttachment(EmailDetails emailDetails);
}
