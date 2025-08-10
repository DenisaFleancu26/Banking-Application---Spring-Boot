package com.bank.banking_application.service.impl;

import com.bank.banking_application.dto.request.CreditDebitRequest;
import com.bank.banking_application.dto.request.EnquiryRequest;
import com.bank.banking_application.dto.request.TransferRequest;
import com.bank.banking_application.dto.response.BankResponse;
import com.bank.banking_application.dto.response.EmailDetails;
import com.bank.banking_application.dto.response.TransactionDTO;
import com.bank.banking_application.entity.User;
import com.bank.banking_application.exception.InsufficientBalanceException;
import com.bank.banking_application.exception.InvalidTransferException;
import com.bank.banking_application.repository.UserRepository;
import com.bank.banking_application.service.interfaces.EmailService;
import com.bank.banking_application.service.interfaces.TransactionService;
import com.bank.banking_application.service.interfaces.UserService;
import com.bank.banking_application.utils.AccountUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public final UserRepository userRepository;
    public final EmailService emailService;
    public final TransactionService transactionService;

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) throws AccountNotFoundException {
        User user = getUserByAccountNumber(enquiryRequest.getAccountNumber());

        return AccountUtils.buildResponse(
                HttpStatus.OK,
                AccountUtils.ACCOUNT_FOUND_MESSAGE,
                AccountUtils.buildAccountInfo(user)
        );
    }

    @Override
    @Transactional
    public BankResponse creditAccount(CreditDebitRequest request) throws AccountNotFoundException {
        User user = getUserByAccountNumber(request.getAccountNumber());

        user.setAccountBalance(user.getAccountBalance().add(request.getAmount()));


        transactionService.saveTransaction(TransactionDTO.builder()
                .accountNumber(user.getAccountNumber())
                .amount(request.getAmount())
                .transactionType("CREDIT")
                .build());

        emailService.sendEmailAlert(buildCreditAlert(user, user, request.getAmount()));
        userRepository.save(user);

        return AccountUtils.buildResponse(
                HttpStatus.OK,
                AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE,
                AccountUtils.buildAccountInfo(user)
        );
    }

    @Override
    @Transactional
    public BankResponse debitAccount(CreditDebitRequest request) throws AccountNotFoundException {
        User user = getUserByAccountNumber(request.getAccountNumber());

        if(user.getAccountBalance().compareTo(request.getAmount()) < 0){
            throw new InsufficientBalanceException("Insufficient balance for debit");
        }

        user.setAccountBalance(user.getAccountBalance().subtract(request.getAmount()));


        transactionService.saveTransaction(TransactionDTO.builder()
                .accountNumber(user.getAccountNumber())
                .amount(request.getAmount())
                .transactionType("DEBIT")
                .build());

        userRepository.save(user);
        emailService.sendEmailAlert(buildDebitAlert(user, user, request.getAmount()));

        return AccountUtils.buildResponse(
                HttpStatus.OK,
                AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE,
                AccountUtils.buildAccountInfo(user)
        );

    }

    @Override
    @Transactional
    public BankResponse transfer(TransferRequest request) throws AccountNotFoundException {

        if(request.getSourceAccountNumber().equals(request.getDestinationAccountNumber())){
            throw new InvalidTransferException(AccountUtils.ERROR_SOURCE_DESTINATION_SAME);
        }

        User sourceUser = getUserByAccountNumber(request.getSourceAccountNumber());
        User destinationUser = getUserByAccountNumber(request.getDestinationAccountNumber());

        if(sourceUser.getAccountBalance().compareTo(request.getAmount()) < 0){
            throw new InsufficientBalanceException(AccountUtils.ERROR_INSUFFICIENT_BALANCE);
        }

        sourceUser.setAccountBalance(sourceUser.getAccountBalance().subtract(request.getAmount()));
        destinationUser.setAccountBalance(destinationUser.getAccountBalance().add(request.getAmount()));

        transactionService.saveTransaction(TransactionDTO.builder()
                .accountNumber(sourceUser.getAccountNumber())
                .amount(request.getAmount())
                .transactionType("DEBIT")
                .build());

        transactionService.saveTransaction(TransactionDTO.builder()
                .accountNumber(destinationUser.getAccountNumber())
                .amount(request.getAmount())
                .transactionType("CREDIT")
                .build());

        userRepository.save(sourceUser);
        userRepository.save(destinationUser);

        emailService.sendEmailAlert(buildDebitAlert(sourceUser, sourceUser, request.getAmount()));
        emailService.sendEmailAlert(buildCreditAlert(destinationUser, sourceUser, request.getAmount()));

        return AccountUtils.buildResponse(HttpStatus.OK, AccountUtils.ACCOUNT_TRANSFER_SUCCESS_MESSAGE, null);
    }

    private User getUserByAccountNumber(String accountNumber) throws AccountNotFoundException {
        return userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(String.format(AccountUtils.ACCOUNT_NOT_FOUND_MESSAGE, accountNumber)));
    }

    private EmailDetails buildDebitAlert(User destinationUser, User sourceUser,  BigDecimal amount){
        return EmailDetails.builder()
                .subject("Debit Alert")
                .recipient(sourceUser.getEmail())
                .messageBody("The sum of " + amount + " has been deducted from your account" +
                        (!destinationUser.equals(sourceUser)
                                ? " to " + destinationUser.getFirstName() + " " + destinationUser.getLastName()
                                : "") +
                        "! Your current balance is " + sourceUser.getAccountBalance())
                .build();
    }

    private EmailDetails buildCreditAlert(User destinationUser, User sourceUser, BigDecimal amount){
        return EmailDetails.builder()
                .subject("Credit Alert")
                .recipient(destinationUser.getEmail())
                .messageBody("The sum of " + amount + " has been sent to your account " +
                        (!destinationUser.equals(sourceUser)
                            ? " from " + sourceUser.getFirstName() + " " + sourceUser.getLastName()
                                : "") +
                        "! Your current balance is " + destinationUser.getAccountBalance())
                .build();
    }
}
