package com.bank.banking_application.service;

import com.bank.banking_application.dto.*;
import com.bank.banking_application.entity.User;
import com.bank.banking_application.repository.UserRepository;
import com.bank.banking_application.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;


    @Override
    public BankResponse createAccount(UserRequest userRequest) {

        if(userRepository.existsByEmail(userRequest.getEmail())){
            return buildResponse(AccountUtils.ACCOUNT_EXISTS_CODE, AccountUtils.ACCOUNT_EXISTS_MESSAGE, null);
        }

        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(newUser);
        // Send email Alert
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(userRequest.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations! Your Account has been successfully created!" +
                        "\nYour account details:" +
                        "\nAccount Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() +
                        "\nAccount Number: " + savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);
        return  buildResponse(
                AccountUtils.ACCOUNT_CREATION_SUCCESS,
                AccountUtils.ACCOUNT_CREATION_MESSAGE,
                buildAccountInfo(savedUser)
        );
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        boolean isAccountExists = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if(!isAccountExists){
            return buildResponse(AccountUtils.ACCOUNT_NOT_EXISTS_CODE, AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE, null);
        }

        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return buildResponse(
                AccountUtils.ACCOUNT_FOUND_CODE,
                AccountUtils.ACCOUNT_FOUND_MESSAGE,
                buildAccountInfo(foundUser)
        );
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExists){
            return buildResponse(AccountUtils.ACCOUNT_NOT_EXISTS_CODE, AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE, null);
        }

        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);

        return buildResponse(
                AccountUtils.ACCOUNT_CREDITED_SUCCESS,
                AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE,
                buildAccountInfo(userToCredit)
        );
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExists){
            return buildResponse(AccountUtils.ACCOUNT_NOT_EXISTS_CODE, AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE, null);
        }

        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        if(userToDebit.getAccountBalance().compareTo(request.getAmount()) < 0){
            return buildResponse(AccountUtils.INSUFFICIENT_BALANCE_CODE, AccountUtils.INSUFFICIENT_BALANCE_MESSAGE, null);
        }else{
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(userToDebit);

            return buildResponse(
                    AccountUtils.ACCOUNT_DEBITED_SUCCESS,
                    AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE,
                    buildAccountInfo(userToDebit)
            );
        }
    }

    private BankResponse buildResponse(String code, String message, AccountInfo info) {
        return BankResponse.builder()
                .responseCode(code)
                .responseMessage(message)
                .accountInfo(info)
                .build();
    }

    private AccountInfo buildAccountInfo(User user) {
        return AccountInfo.builder()
                .accountName(user.getFirstName() + " " + user.getLastName())
                .accountNumber(user.getAccountNumber())
                .accountBalance(user.getAccountBalance())
                .build();
    }
}
