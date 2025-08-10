package com.bank.banking_application.service.interfaces;

import com.bank.banking_application.dto.response.TransactionDTO;

public interface TransactionService {

    void saveTransaction(TransactionDTO transactionDTO);
}
