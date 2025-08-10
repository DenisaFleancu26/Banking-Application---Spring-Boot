package com.bank.banking_application.controller;

import com.bank.banking_application.entity.Transaction;
import com.bank.banking_application.service.impl.BankStatementService;
import com.itextpdf.text.DocumentException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/bankStatement")
@AllArgsConstructor
public class TransactionController {

    private final BankStatementService bankStatementService;

    @GetMapping
    public ResponseEntity<String> generateBankStatement(@RequestParam String accountNumber,
                                                                  @RequestParam String startDate,
                                                                  @RequestParam String endDate) throws DocumentException, IOException, AccountNotFoundException {
        return ResponseEntity.ok(bankStatementService.generateStatement(accountNumber, startDate, endDate));
    }
}
