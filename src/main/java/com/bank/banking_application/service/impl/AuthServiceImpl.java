package com.bank.banking_application.service.impl;

import com.bank.banking_application.config.JwtTokenProvider;
import com.bank.banking_application.dto.*;
import com.bank.banking_application.dto.request.UserRequest;
import com.bank.banking_application.dto.response.BankResponse;
import com.bank.banking_application.entity.User;
import com.bank.banking_application.repository.UserRepository;
import com.bank.banking_application.service.interfaces.EmailService;
import com.bank.banking_application.service.interfaces.AuthService;
import com.bank.banking_application.utils.AccountUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public BankResponse login(LoginDTO loginDto){
        Authentication authentication = null;
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        EmailDetails loginAlert = EmailDetails.builder()
                .subject("You're logged in!")
                .recipient(loginDto.getEmail())
                .messageBody("You logged into your account! If you did not initiate this request, please contact your bank!")
                .build();
        emailService.sendEmailAlert(loginAlert);
        return BankResponse.builder()
                .responseCode("Login Success")
                .responseMessage(jwtTokenProvider.generateToken(authentication))
                .build();
    }

    @Override
    public BankResponse register(UserRequest userRequest) {
        if(userRepository.existsByEmail(userRequest.getEmail())){
            return AccountUtils.buildResponse(AccountUtils.ACCOUNT_EXISTS_CODE, AccountUtils.ACCOUNT_EXISTS_MESSAGE, null);
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
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .phoneNumber(userRequest.getPhoneNumber())
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(newUser);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userRequest.getEmail(), userRequest.getPassword())
        );

        String token = jwtTokenProvider.generateToken(authentication);

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(userRequest.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations! Your Account has been successfully created!" +
                        "\nYour account details:" +
                        "\nAccount Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() +
                        "\nAccount Number: " + savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);
        return  AccountUtils.buildResponse(
                AccountUtils.ACCOUNT_CREATION_MESSAGE,
                token,
                AccountUtils.buildAccountInfo(savedUser)
        );
    }



}
