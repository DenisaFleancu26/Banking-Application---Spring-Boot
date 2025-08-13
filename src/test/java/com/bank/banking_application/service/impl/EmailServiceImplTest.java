package com.bank.banking_application.service.impl;

import com.bank.banking_application.dto.response.EmailDetails;
import com.bank.banking_application.exception.EmailSendingException;
import com.bank.banking_application.service.interfaces.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    private EmailService underTest;

    @Mock
    private JavaMailSender javaMailSender;

    @BeforeEach
    void setUp() {
        underTest = new EmailServiceImpl(javaMailSender);
    }

    @Test
    void sendEmailAlert() {
        EmailDetails emailDetails = new EmailDetails(
                "recipient@gmail.com",
                "Hello",
                "Test",
                null
        );

        underTest.sendEmailAlert(emailDetails);

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void throwEmailSendingException(){
        EmailDetails emailDetails = new EmailDetails(
                "recipient@gmail.com",
                "Hello",
                "Test",
                null
        );

        doThrow(new MailException("Error") {}).when(javaMailSender).send(any(SimpleMailMessage.class));

        assertThatThrownBy(() -> underTest.sendEmailAlert(emailDetails))
                .isInstanceOf(EmailSendingException.class)
                .hasMessageContaining("Error sending email to " + emailDetails.getRecipient());
    }


}