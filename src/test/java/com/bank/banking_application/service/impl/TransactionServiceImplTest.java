package com.bank.banking_application.service.impl;

import com.bank.banking_application.dto.response.TransactionDTO;
import com.bank.banking_application.repository.TransactionRepository;
import com.bank.banking_application.service.interfaces.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    private TransactionService underTest;

    @Mock
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp(){
        underTest = new TransactionServiceImpl(
                transactionRepository);
    }

    @Test
    void saveTransaction() {

        TransactionDTO transactionDTO = TransactionDTO.builder()
                .accountNumber("2025123456")
                .amount(new BigDecimal("100"))
                .transactionType("CREDIT")
                .build();
        underTest.saveTransaction(transactionDTO);

        verify(transactionRepository, times(1)).save(argThat(transaction ->
                transaction.getTransactionType().equals("CREDIT") &&
                transaction.getAccountNumber().equals(transactionDTO.getAccountNumber()) &&
                transaction.getAmount().compareTo(transactionDTO.getAmount()) == 0 &&
                transaction.getStatus().equals("SUCCESS")
        ));
    }
}