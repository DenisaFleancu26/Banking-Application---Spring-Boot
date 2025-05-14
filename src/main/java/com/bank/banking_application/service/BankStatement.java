package com.bank.banking_application.service;

import com.bank.banking_application.entity.Transaction;
import com.bank.banking_application.repository.TransactionRepository;
import com.itextpdf.text.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
public class BankStatement {

    private TransactionRepository transactionRepository;

    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws DocumentException, IOException {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

        return transactionRepository
                .findByAccountNumberAndCreatedAtBetween(accountNumber, start, end);
    }
}