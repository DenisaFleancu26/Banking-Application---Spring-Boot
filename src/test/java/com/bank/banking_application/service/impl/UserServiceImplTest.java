package com.bank.banking_application.service.impl;

import com.bank.banking_application.dto.request.CreditDebitRequest;
import com.bank.banking_application.dto.request.EnquiryRequest;
import com.bank.banking_application.dto.request.TransferRequest;
import com.bank.banking_application.dto.response.AccountInfo;
import com.bank.banking_application.dto.response.BankResponse;
import com.bank.banking_application.entity.User;
import com.bank.banking_application.exception.InsufficientBalanceException;
import com.bank.banking_application.exception.InvalidTransferException;
import com.bank.banking_application.repository.UserRepository;
import com.bank.banking_application.service.interfaces.EmailService;
import com.bank.banking_application.service.interfaces.TransactionService;
import com.bank.banking_application.service.interfaces.UserService;
import com.bank.banking_application.utils.AccountUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserService underTest;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private TransactionService transactionService;

    private static final String ACCOUNT_NUMBER_1 = "2025123456";
    private static final String ACCOUNT_NUMBER_2 = "2025234567";

    @BeforeEach
    void setUp(){
        underTest = new UserServiceImpl(
                userRepository,
                emailService,
                transactionService);
    }

    @Test
    void balanceEnquiry() throws AccountNotFoundException {

        User user = createUser();

        when(userRepository.findByAccountNumber(user.getAccountNumber())).thenReturn(Optional.of(user));

        EnquiryRequest enquiryRequest = new EnquiryRequest(ACCOUNT_NUMBER_1);

        BankResponse response = underTest.balanceEnquiry(enquiryRequest);

        assertThat(response.getResponseCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getResponseMessage()).isEqualTo(AccountUtils.ACCOUNT_FOUND_MESSAGE);
        assertThat(response.getAccountInfo()).isNotNull();

        AccountInfo accountInfo = (AccountInfo) response.getAccountInfo();
        assertThat(accountInfo.getAccountBalance()).isEqualTo(user.getAccountBalance());
        assertThat(accountInfo.getAccountNumber()).isEqualTo(user.getAccountNumber());

        verify(userRepository, times(1)).findByAccountNumber(ACCOUNT_NUMBER_1);
    }

    @Test
    void creditAccount() throws AccountNotFoundException {

        User user = createUser();
        BigDecimal amount = new BigDecimal("100");

        when(userRepository.findByAccountNumber(user.getAccountNumber())).thenReturn(Optional.of(user));

        CreditDebitRequest creditDebitRequest = new CreditDebitRequest(ACCOUNT_NUMBER_1, amount);
        BankResponse bankResponse = underTest.creditAccount(creditDebitRequest);

        assertThat(user.getAccountBalance()).isEqualByComparingTo(new BigDecimal("5100"));
        verify(userRepository, times(1)).save(user);

        verify(transactionService, times(1)).saveTransaction(argThat(transactionDTO ->
                transactionDTO.getAccountNumber().equals(ACCOUNT_NUMBER_1) &&
                        transactionDTO.getAmount().compareTo(new BigDecimal("100")) == 0 &&
                        "CREDIT".equals(transactionDTO.getTransactionType())
        ));

        verify(emailService, times(1)).sendEmailAlert(argThat(emailDetails ->
                emailDetails.getRecipient().equals(user.getEmail()) &&
                        emailDetails.getSubject().contains("Credit Alert") &&
                        emailDetails.getMessageBody().contains("has been sent")
        ));

        assertThat(bankResponse.getResponseCode()).isEqualTo(HttpStatus.OK);
        assertThat(bankResponse.getResponseMessage()).isEqualTo(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE);
        assertThat(bankResponse.getAccountInfo()).isNotNull();

        AccountInfo accountInfo = (AccountInfo) bankResponse.getAccountInfo();
        assertThat(accountInfo.getAccountBalance()).isEqualByComparingTo(new BigDecimal("5100"));
        assertThat(accountInfo.getAccountNumber()).isEqualTo(ACCOUNT_NUMBER_1);
    }

    @Test
    void debitAccount() throws AccountNotFoundException {
        User user = createUser();
        BigDecimal amount = new BigDecimal("100");

        when(userRepository.findByAccountNumber(user.getAccountNumber())).thenReturn(Optional.of(user));

        CreditDebitRequest creditDebitRequest = new CreditDebitRequest(ACCOUNT_NUMBER_1, amount);
        BankResponse bankResponse = underTest.debitAccount(creditDebitRequest);

        assertThat(user.getAccountBalance()).isEqualByComparingTo(new BigDecimal("4900"));
        verify(userRepository, times(1)).save(user);

        verify(transactionService, times(1)).saveTransaction(argThat(transactionDTO ->
                transactionDTO.getAccountNumber().equals(ACCOUNT_NUMBER_1) &&
                        transactionDTO.getAmount().compareTo(new BigDecimal("100")) == 0 &&
                        "DEBIT".equals(transactionDTO.getTransactionType())
        ));

        verify(emailService, times(1)).sendEmailAlert(argThat(emailDetails ->
                emailDetails.getRecipient().equals(user.getEmail()) &&
                        emailDetails.getSubject().contains("Debit Alert") &&
                        emailDetails.getMessageBody().contains("has been deducted")
        ));

        assertThat(bankResponse.getResponseCode()).isEqualTo(HttpStatus.OK);
        assertThat(bankResponse.getResponseMessage()).isEqualTo(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE);
        assertThat(bankResponse.getAccountInfo()).isNotNull();

        AccountInfo accountInfo = (AccountInfo) bankResponse.getAccountInfo();
        assertThat(accountInfo.getAccountBalance()).isEqualByComparingTo(new BigDecimal("4900"));
        assertThat(accountInfo.getAccountNumber()).isEqualTo(ACCOUNT_NUMBER_1);
    }

    @Test
    void transfer() throws AccountNotFoundException {
        User sourceUser = createUser();

        User destinationUser = User.builder()
                .accountNumber(ACCOUNT_NUMBER_2)
                .accountBalance(new BigDecimal("500"))
                .email("destination@gmail.com")
                .build();

        BigDecimal transferAmount = new BigDecimal("200");

        when(userRepository.findByAccountNumber(ACCOUNT_NUMBER_1)).thenReturn(Optional.of(sourceUser));
        when(userRepository.findByAccountNumber(ACCOUNT_NUMBER_2)).thenReturn(Optional.of(destinationUser));

        TransferRequest request = new TransferRequest();
        request.setSourceAccountNumber(sourceUser.getAccountNumber());
        request.setDestinationAccountNumber(destinationUser.getAccountNumber());
        request.setAmount(transferAmount);

        BankResponse response = underTest.transfer(request);

        assertThat(sourceUser.getAccountBalance()).isEqualByComparingTo(new BigDecimal("4800"));
        assertThat(destinationUser.getAccountBalance()).isEqualByComparingTo(new BigDecimal("700"));

        verify(userRepository, times(1)).save(sourceUser);
        verify(userRepository, times(1)).save(destinationUser);

        verify(transactionService, times(1)).saveTransaction(argThat(t ->
                t.getAccountNumber().equals(ACCOUNT_NUMBER_1) &&
                        t.getAmount().compareTo(transferAmount) == 0 &&
                        "DEBIT".equals(t.getTransactionType())
        ));
        verify(transactionService, times(1)).saveTransaction(argThat(t ->
                t.getAccountNumber().equals(ACCOUNT_NUMBER_2) &&
                        t.getAmount().compareTo(transferAmount) == 0 &&
                        "CREDIT".equals(t.getTransactionType())
        ));

        verify(emailService, times(1)).sendEmailAlert(argThat(email ->
                email.getRecipient().equals(sourceUser.getEmail()) &&
                        email.getSubject().contains("Debit Alert")
        ));
        verify(emailService, times(1)).sendEmailAlert(argThat(email ->
                email.getRecipient().equals(destinationUser.getEmail()) &&
                        email.getSubject().contains("Credit Alert")
        ));

        assertThat(response.getResponseCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getResponseMessage()).isEqualTo(AccountUtils.ACCOUNT_TRANSFER_SUCCESS_MESSAGE);
        assertThat(response.getAccountInfo()).isNull();
    }


    @Test
    void throwsAccountNotFoundException(){

        when(userRepository.findByAccountNumber(ACCOUNT_NUMBER_1)).thenReturn(Optional.empty());

        EnquiryRequest enquiryRequest = new EnquiryRequest(ACCOUNT_NUMBER_1);

        assertThatThrownBy(() -> underTest.balanceEnquiry(enquiryRequest))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining(String.format(AccountUtils.ACCOUNT_NOT_FOUND_MESSAGE, ACCOUNT_NUMBER_1));

        verify(userRepository, times(1)).findByAccountNumber(ACCOUNT_NUMBER_1);
    }

    @Test
    void throwInsufficientBalanceException(){
        User user = createUser();
        BigDecimal amount = new BigDecimal("6000");

        CreditDebitRequest creditDebitRequest = new CreditDebitRequest(user.getAccountNumber(), amount);

        when(userRepository.findByAccountNumber(user.getAccountNumber())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> underTest.debitAccount(creditDebitRequest))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining(AccountUtils.ERROR_INSUFFICIENT_BALANCE);
    }

    @Test
    void throwInvalidTransferException(){

        TransferRequest request = new TransferRequest();
        request.setSourceAccountNumber(ACCOUNT_NUMBER_1);
        request.setDestinationAccountNumber(ACCOUNT_NUMBER_1);
        request.setAmount(new BigDecimal("100"));

        assertThatThrownBy(() -> underTest.transfer(request))
                .isInstanceOf(InvalidTransferException.class)
                .hasMessageContaining(AccountUtils.ERROR_SOURCE_DESTINATION_SAME);
    }

    private User createUser(){
        return User.builder()
                .accountNumber(ACCOUNT_NUMBER_1)
                .email("test@gmail.com")
                .accountBalance(new BigDecimal("5000"))
                .build();
    }
}