package com.bank.banking_application.utils;

import com.bank.banking_application.dto.response.AccountInfo;
import com.bank.banking_application.dto.response.BankResponse;
import com.bank.banking_application.entity.User;
import com.bank.banking_application.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.Year;
import java.util.concurrent.ThreadLocalRandom;

public class AccountUtils {

    public static final String ACCOUNT_EXISTS_MESSAGE = "This user already has an account created!";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account has been successfully created!";
    public static final String ACCOUNT_NOT_FOUND_MESSAGE = "Account number %s not found!";
    public static final String ACCOUNT_FOUND_MESSAGE = "User account found!";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "User account has been credited!";
    public static final String ACCOUNT_DEBITED_SUCCESS_MESSAGE = "Account has been successfully debited!";
    public static final String ACCOUNT_TRANSFER_SUCCESS_MESSAGE = "Amount has been transferred!";
    public static final String ERROR_START_DATE_AFTER_END_DATE = "Start date must be before or equal to end date";
    public static final String ERROR_SOURCE_DESTINATION_SAME = "Source and destination account numbers must be different";
    public static final String ERROR_INSUFFICIENT_BALANCE = "Insufficient balance for transfer";

    public static String generateAccountNumber(){

        Year currentYear = Year.now();
        int min = 100000;
        int max = 1000000;

        int randomNum = ThreadLocalRandom.current().nextInt(min, max);

        return String.valueOf(currentYear) + String.valueOf(randomNum);
    }

    public static BankResponse buildResponse(HttpStatus code, String message, AccountInfo info) {
        return BankResponse.builder()
                .responseCode(code)
                .responseMessage(message)
                .accountInfo(info)
                .build();
    }

    public static AccountInfo buildAccountInfo(User user) {
        return AccountInfo.builder()
                .accountName(user.getFirstName() + " " + user.getLastName())
                .accountNumber(user.getAccountNumber())
                .accountBalance(user.getAccountBalance())
                .build();
    }

}
