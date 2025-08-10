package com.bank.banking_application.service.impl;

import com.bank.banking_application.config.JwtTokenProvider;
import com.bank.banking_application.dto.request.LoginRequest;
import com.bank.banking_application.dto.request.UserRequest;
import com.bank.banking_application.dto.response.BankResponse;
import com.bank.banking_application.dto.response.EmailDetails;
import com.bank.banking_application.entity.User;
import com.bank.banking_application.repository.UserRepository;
import com.bank.banking_application.service.interfaces.EmailService;
import com.bank.banking_application.service.interfaces.AuthService;
import com.bank.banking_application.utils.AccountUtils;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public BankResponse login(LoginRequest loginRequest){
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            EmailDetails loginAlert = EmailDetails.builder()
                    .subject("You're logged in!")
                    .recipient(loginRequest.getEmail())
                    .messageBody("You logged into your account! If you did not initiate this request, please contact your bank!")
                    .build();
            emailService.sendEmailAlert(loginAlert);

            return BankResponse.builder()
                    .responseCode(HttpStatus.OK)
                    .responseMessage(jwtTokenProvider.generateToken(authentication))
                    .build();

    }

    @Override
    public BankResponse register(UserRequest userRequest) {
        if(userRepository.existsByEmail(userRequest.getEmail())){
            throw new IllegalArgumentException(AccountUtils.ACCOUNT_EXISTS_MESSAGE);
        }

        String accountNumber;
        do {
            accountNumber = AccountUtils.generateAccountNumber();
        } while (userRepository.existsByAccountNumber(accountNumber));

        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(accountNumber)
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
                .messageBody(AccountUtils.ACCOUNT_CREATION_MESSAGE +
                        "\nYour account details:" +
                        "\nAccount Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() +
                        "\nAccount Number: " + savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);
        return  AccountUtils.buildResponse(
                HttpStatus.OK,
                token,
                AccountUtils.buildAccountInfo(savedUser)
        );
    }
}
