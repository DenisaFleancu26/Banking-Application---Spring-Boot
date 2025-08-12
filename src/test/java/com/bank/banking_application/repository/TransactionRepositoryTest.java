package com.bank.banking_application.repository;

import com.bank.banking_application.entity.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findTransactionsByAccountNumberAndCreatedAtBetween() {

        String accountNumber = "2025123456";

        Transaction tx1 = Transaction.builder()
                .transactionType("CREDIT")
                .amount(new BigDecimal("100.00"))
                .accountNumber(accountNumber)
                .status("SUCCESS")
                .build();

        Transaction tx2 = Transaction.builder()
                .transactionType("DEBIT")
                .amount(new BigDecimal("50.00"))
                .accountNumber(accountNumber)
                .status("SUCCESS")
                .build();

        Transaction tx3 = Transaction.builder()
                .transactionType("TRANSFER")
                .amount(new BigDecimal("200.00"))
                .accountNumber(accountNumber)
                .status("SUCCESS")
                .build();

        underTest.save(tx1);
        underTest.save(tx2);
        underTest.save(tx3);

        LocalDate today = LocalDate.now();

        List<Transaction> transactions = underTest.findByAccountNumberAndCreatedAtBetween(accountNumber, today.minusDays(1), today.plusDays(1));

        assertThat(transactions).hasSize(3)
                .allMatch(tx -> tx.getAccountNumber().equals(accountNumber));

    }
}