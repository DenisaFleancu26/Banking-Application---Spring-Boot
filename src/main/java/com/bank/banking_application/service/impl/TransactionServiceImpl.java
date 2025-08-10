package com.bank.banking_application.service.impl;

import com.bank.banking_application.dto.response.TransactionDTO;
import com.bank.banking_application.entity.Transaction;
import com.bank.banking_application.repository.TransactionRepository;
import com.bank.banking_application.service.interfaces.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public void saveTransaction(TransactionDTO transactionDTO) {
        transactionRepository.save(Transaction.builder()
                .transactionType(transactionDTO.getTransactionType())
                .accountNumber(transactionDTO.getAccountNumber())
                .amount(transactionDTO.getAmount())
                .status("SUCCESS")
                .build());
    }
}
