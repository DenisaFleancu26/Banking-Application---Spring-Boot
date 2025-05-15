package com.bank.banking_application.service.impl;

import com.bank.banking_application.config.JwtTokenProvider;
import com.bank.banking_application.dto.*;
import com.bank.banking_application.dto.request.CreditDebitRequest;
import com.bank.banking_application.dto.request.EnquiryRequest;
import com.bank.banking_application.dto.request.TransferRequest;
import com.bank.banking_application.dto.response.BankResponse;
import com.bank.banking_application.entity.User;
import com.bank.banking_application.repository.UserRepository;
import com.bank.banking_application.service.interfaces.EmailService;
import com.bank.banking_application.service.interfaces.TransactionService;
import com.bank.banking_application.service.interfaces.UserService;
import com.bank.banking_application.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        if (!userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber())){
            return AccountUtils.buildResponse(AccountUtils.ACCOUNT_NOT_EXISTS_CODE, AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE, null);
        }

        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return AccountUtils.buildResponse(
                AccountUtils.ACCOUNT_FOUND_CODE,
                AccountUtils.ACCOUNT_FOUND_MESSAGE,
                AccountUtils.buildAccountInfo(foundUser)
        );
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        if (!userRepository.existsByAccountNumber(request.getAccountNumber())){
            return AccountUtils.buildResponse(AccountUtils.ACCOUNT_NOT_EXISTS_CODE, AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE, null);
        }

        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);

        TransactionDTO transactionDTO = TransactionDTO.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .amount(request.getAmount())
                .transactionType("CREDIT")
                .build();
        transactionService.saveTransaction(transactionDTO);

        return AccountUtils.buildResponse(
                AccountUtils.ACCOUNT_CREDITED_SUCCESS,
                AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE,
                AccountUtils.buildAccountInfo(userToCredit)
        );
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        if (!userRepository.existsByAccountNumber(request.getAccountNumber())){
            return AccountUtils.buildResponse(AccountUtils.ACCOUNT_NOT_EXISTS_CODE, AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE, null);
        }

        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        if(userToDebit.getAccountBalance().compareTo(request.getAmount()) < 0){
            return AccountUtils.buildResponse(AccountUtils.INSUFFICIENT_BALANCE_CODE, AccountUtils.INSUFFICIENT_BALANCE_MESSAGE, null);
        }

        userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(userToDebit);

        TransactionDTO transactionDTO = TransactionDTO.builder()
                .accountNumber(userToDebit.getAccountNumber())
                .amount(request.getAmount())
                .transactionType("DEBIT")
                .build();
        transactionService.saveTransaction(transactionDTO);

        return AccountUtils.buildResponse(
                AccountUtils.ACCOUNT_DEBITED_SUCCESS,
                AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE,
                AccountUtils.buildAccountInfo(userToDebit)
        );

    }

    @Override
    public BankResponse transfer(TransferRequest request) {

        User sourceUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        if(sourceUser.getAccountBalance().compareTo(request.getAmount()) < 0){
            return AccountUtils.buildResponse(AccountUtils.INSUFFICIENT_BALANCE_CODE, AccountUtils.INSUFFICIENT_BALANCE_MESSAGE, null);
        }

        if(!userRepository.existsByAccountNumber(request.getDestinationAccountNumber())){
            return AccountUtils.buildResponse(AccountUtils.ACCOUNT_NOT_EXISTS_CODE, AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE, null);
        }
        User destinationUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());

        processTransfer(sourceUser, destinationUser, request.getAmount());

        TransactionDTO transactionDTO1 = TransactionDTO.builder()
                .accountNumber(sourceUser.getAccountNumber())
                .amount(request.getAmount())
                .transactionType("DEBIT")
                .build();
        transactionService.saveTransaction(transactionDTO1);

        TransactionDTO transactionDTO2 = TransactionDTO.builder()
                .accountNumber(destinationUser.getAccountNumber())
                .amount(request.getAmount())
                .transactionType("CREDIT")
                .build();
        transactionService.saveTransaction(transactionDTO2);

        return AccountUtils.buildResponse(AccountUtils.ACCOUNT_TRANSFER_SUCCESS, AccountUtils.ACCOUNT_TRANSFER_SUCCESS_MESSAGE, null);
    }

    private void processTransfer(User sourceUser, User destinationUser, BigDecimal amount){
        sourceUser.setAccountBalance(sourceUser.getAccountBalance().subtract(amount));
        destinationUser.setAccountBalance(destinationUser.getAccountBalance().add(amount));

        userRepository.save(sourceUser);
        userRepository.save(destinationUser);

        EmailDetails debitAlert = EmailDetails.builder()
                .subject("DEBIT ALERT")
                .recipient(sourceUser.getEmail())
                .messageBody("The sum of " + amount
                        + " has been deducted from your account! Your current balance is "
                        + sourceUser.getAccountBalance())
                .build();

        EmailDetails creditAlert = EmailDetails.builder()
                .subject("CREDIT ALERT")
                .recipient(destinationUser.getEmail())
                .messageBody("The sum of " + amount + " has been sent to your account from "
                        + sourceUser.getFirstName() + " " + sourceUser.getLastName()
                        + ". Your current balance is "
                        + destinationUser.getAccountBalance())
                .build();

        emailService.sendEmailAlert(debitAlert);
        emailService.sendEmailAlert(creditAlert);
    }
}
