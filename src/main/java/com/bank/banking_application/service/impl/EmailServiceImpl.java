package com.bank.banking_application.service.impl;

import com.bank.banking_application.exception.EmailSendingException;
import com.bank.banking_application.service.interfaces.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.bank.banking_application.dto.response.EmailDetails;

import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    @Async
    public void sendEmailAlert(EmailDetails emailDetails) {
        try{
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(senderEmail);
            mailMessage.setTo(emailDetails.getRecipient());
            mailMessage.setText(emailDetails.getMessageBody());
            mailMessage.setSubject(emailDetails.getSubject());

            javaMailSender.send(mailMessage);
            log.info("Email sent successfully to {}", emailDetails.getRecipient());
        } catch(MailException e){
            throw new EmailSendingException("Error sending email to " + emailDetails.getRecipient());
        }
    }

    @Override
    @Async
    public void sendEmailWithAttachment(EmailDetails emailDetails) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try{
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(senderEmail);
            mimeMessageHelper.setTo(emailDetails.getRecipient());
            mimeMessageHelper.setText(emailDetails.getMessageBody());
            mimeMessageHelper.setSubject(emailDetails.getSubject());

            File attachment = new File(emailDetails.getAttachment());
            if(attachment.exists() && attachment.isFile()){
                FileSystemResource file = new FileSystemResource(attachment);
                mimeMessageHelper.addAttachment(file.getFilename(), file);
            }else{
                log.warn("Attachment not found: {}", emailDetails.getAttachment());
            }

            javaMailSender.send(mimeMessage);
            log.info("Email with attachment sent successfully to {}", emailDetails.getRecipient());
        } catch (MessagingException e) {
            log.error("Failed to send email with attachment to {}", emailDetails.getRecipient(), e);
            throw new EmailSendingException("Error sending email with attachment to " + emailDetails.getRecipient());
        }
    }
}
