package com.bank.banking_application.service.impl;

import com.bank.banking_application.dto.response.EmailDetails;
import com.bank.banking_application.entity.Transaction;
import com.bank.banking_application.entity.User;
import com.bank.banking_application.repository.TransactionRepository;
import com.bank.banking_application.repository.UserRepository;
import com.bank.banking_application.service.interfaces.EmailService;
import com.bank.banking_application.utils.AccountUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankStatementService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${bank.statement.file}")
    private String folderPath;

    public String generateStatement(String accountNumber, String startDate, String endDate) throws DocumentException, IOException, AccountNotFoundException {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

        if (start.isAfter(end)) {
            throw new IllegalArgumentException(AccountUtils.ERROR_START_DATE_AFTER_END_DATE);
        }

        List<Transaction> transactions = transactionRepository
                .findByAccountNumberAndCreatedAtBetween(accountNumber, start, end);

        User user = userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(String.format(AccountUtils.ACCOUNT_NOT_FOUND_MESSAGE, accountNumber)));

        String uniqueFileName = folderPath + "bank_statement_" + accountNumber + "_" + UUID.randomUUID() + ".pdf";
        designStatement(transactions, startDate, endDate, user, uniqueFileName);

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("STATEMENT OF ACCOUNT")
                .messageBody("Kindly find your request account statement attached!")
                .attachment(uniqueFileName)
                .build();
        emailService.sendEmailWithAttachment(emailDetails);

        return "Statement sent successfully to " + user.getEmail();
    }

    private void designStatement(List<Transaction> transactions, String startDate, String endDate, User user, String filePath ) throws IOException, DocumentException {

        final Document document = new Document(PageSize.A4);

        OutputStream outputStream = new FileOutputStream(filePath);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable bankInfoTable = new PdfPTable(1);
        bankInfoTable.addCell(cell("Banking Application",Element.ALIGN_LEFT,20,true, BaseColor.WHITE, 15, BaseColor.BLUE, false));
        bankInfoTable.addCell(cell("Targu-Jiu, Gorj, Romania", Element.ALIGN_LEFT,10,false, BaseColor.BLACK, 0, null, false));
        document.add(bankInfoTable);

        PdfPTable statementInfoTable = new PdfPTable(1);
        statementInfoTable.addCell(cell("STATEMENT ACCOUNT", Element.ALIGN_LEFT,15,true, BaseColor.BLACK, 10, null, false));
        document.add(statementInfoTable);

        PdfPTable statementInfo = new PdfPTable(2);
        statementInfo.addCell(cell("Start Date: " + startDate, false, 2));
        statementInfo.addCell(cell("Customer Name: " + user.getFirstName() + " "  + user.getLastName(), false, 2));
        statementInfo.addCell(cell("End Date: " + endDate, false, 2));
        statementInfo.addCell(cell("Customer Address: " + user.getAddress(), false, 2));
        statementInfo.addCell(cell("", false, 2));
        statementInfo.addCell(cell("Customer State: " + user.getStateOfOrigin(), false, 2));
        document.add(statementInfo);

        PdfPTable transactionTable = new PdfPTable(4);
        transactionTable.setSpacingBefore(15f);
        transactionTable.addCell(cell("DATE",Element.ALIGN_CENTER,10,false, BaseColor.WHITE, 5, BaseColor.BLUE, false ));
        transactionTable.addCell(cell("TRANSACTION TYPE",Element.ALIGN_CENTER,10,false, BaseColor.WHITE, 5, BaseColor.BLUE, false));
        transactionTable.addCell(cell("TRANSACTION AMOUNT",Element.ALIGN_CENTER,10,false, BaseColor.WHITE, 5, BaseColor.BLUE, false));
        transactionTable.addCell(cell("STATUS",Element.ALIGN_CENTER,10,false, BaseColor.WHITE, 5, BaseColor.BLUE, false));

        transactions.forEach(transaction -> {
            transactionTable.addCell(cell(transaction.getCreatedAt().toString(), true, 5));
            transactionTable.addCell(cell(transaction.getTransactionType(), true, 5));
            transactionTable.addCell(cell(transaction.getAmount().toString() + " RON", true, 5));
            transactionTable.addCell(cell(transaction.getStatus(), true, 5));
        });
        document.add(transactionTable);

        PdfPTable accountInfo = new PdfPTable(2);
        accountInfo.setSpacingBefore(15f);
        accountInfo.addCell(cell("Account Number: " + user.getAccountNumber(),Element.ALIGN_LEFT,10,true, BaseColor.BLACK, 0, null, false));
        accountInfo.addCell(cell("Account Balance: " + user.getAccountBalance() + " RON",Element.ALIGN_LEFT,10,true, BaseColor.BLACK, 0, null, false));
        document.add(accountInfo);

        document.close();
        outputStream.close();

    }

    public static PdfPCell cell(String text, boolean border, int padding) {
        return cell(text, Element.ALIGN_LEFT, 10, false, null, padding, null, border);
    }

    public static PdfPCell cell(String text, int alignment, float size, boolean bold, BaseColor textColor, float padding, BaseColor bgColor, boolean border) {
        Font customFont = new Font(
                Font.FontFamily.HELVETICA,
                size,
                bold ? Font.BOLD : Font.NORMAL,
                textColor
        );

        PdfPCell cell = new PdfPCell(new Phrase(text, customFont));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(padding);
        cell.setBorder( border ? Rectangle.BOX : 0);

        if (bgColor != null) {
            cell.setBackgroundColor(bgColor);
        }

        return cell;
    }

}
