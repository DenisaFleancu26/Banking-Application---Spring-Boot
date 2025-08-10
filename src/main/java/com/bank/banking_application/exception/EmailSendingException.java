package com.bank.banking_application.exception;

import jakarta.mail.MessagingException;
import org.springframework.mail.MailException;

public class EmailSendingException extends RuntimeException{

    public EmailSendingException(String message){
        super(message);
    }
}
