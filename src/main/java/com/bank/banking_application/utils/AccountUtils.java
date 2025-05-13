package com.bank.banking_application.utils;

import java.time.Year;
import java.util.concurrent.ThreadLocalRandom;

public class AccountUtils {

    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user already has an account created!";

    public static final String ACCOUNT_CREATION_SUCCESS = "002";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account has been successfully created!";

    public static final String ACCOUNT_NOT_EXISTS_CODE = "003";
    public static final String ACCOUNT_NOT_EXISTS_MESSAGE = "User with the provided account number dose not exists!";

    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_FOUND_MESSAGE = "User account found!";

    public static final String ACCOUNT_CREDITED_SUCCESS = "005";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "User account has been credited!";

    public static final String INSUFFICIENT_BALANCE_CODE = "006";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient balance!";

    public static final String ACCOUNT_DEBITED_SUCCESS = "007";
    public static final String ACCOUNT_DEBITED_SUCCESS_MESSAGE = "Account has been successfully debited!";

    public static final String ACCOUNT_TRANSFER_SUCCESS = "008";
    public static final String ACCOUNT_TRANSFER_SUCCESS_MESSAGE = "Amount has been transferred!";

    public static String generateAccountNumber(){

        // current year + random six digits
        Year currentYear = Year.now();
        int min = 100000;
        int max = 1000000;

        // generate a random number between min & max
        int randomNum = ThreadLocalRandom.current().nextInt(min, max);

        return String.valueOf(currentYear) + String.valueOf(randomNum);
    }

}
