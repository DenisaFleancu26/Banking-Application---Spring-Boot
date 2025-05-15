package com.bank.banking_application.service.interfaces;

import com.bank.banking_application.dto.request.CreditDebitRequest;
import com.bank.banking_application.dto.request.EnquiryRequest;
import com.bank.banking_application.dto.request.TransferRequest;
import com.bank.banking_application.dto.response.BankResponse;

public interface UserService {

    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);

    BankResponse creditAccount(CreditDebitRequest request);

    BankResponse debitAccount(CreditDebitRequest request);

    BankResponse transfer(TransferRequest request);

}

