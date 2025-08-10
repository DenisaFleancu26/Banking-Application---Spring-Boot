package com.bank.banking_application.service.interfaces;

import com.bank.banking_application.dto.request.CreditDebitRequest;
import com.bank.banking_application.dto.request.EnquiryRequest;
import com.bank.banking_application.dto.request.TransferRequest;
import com.bank.banking_application.dto.response.BankResponse;

import javax.security.auth.login.AccountNotFoundException;

public interface UserService {

    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) throws AccountNotFoundException;

    BankResponse creditAccount(CreditDebitRequest request) throws AccountNotFoundException;

    BankResponse debitAccount(CreditDebitRequest request) throws AccountNotFoundException;

    BankResponse transfer(TransferRequest request) throws AccountNotFoundException;

}

