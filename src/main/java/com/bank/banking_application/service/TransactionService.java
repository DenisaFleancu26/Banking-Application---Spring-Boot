package com.bank.banking_application.service;

import com.bank.banking_application.dto.TransactionDTO;
import com.bank.banking_application.entity.Transaction;

public interface TransactionService {

    void saveTransaction(TransactionDTO transactionDTO);
}
