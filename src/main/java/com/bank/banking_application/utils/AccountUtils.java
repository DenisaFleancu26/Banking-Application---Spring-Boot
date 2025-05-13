package com.bank.banking_application.utils;

import java.time.Year;
import java.util.concurrent.ThreadLocalRandom;

public class AccountUtils {

    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user already has an account created!";
    public static final String ACCOUNT_CREATION_SUCCESS = "002";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account has been successfully created!";

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
