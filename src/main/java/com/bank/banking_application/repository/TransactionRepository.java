package com.bank.banking_application.repository;

import com.bank.banking_application.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByAccountNumberAndCreatedAtBetween(String accountNumber, LocalDate startDate, LocalDate endDate);
}
