package com.bank.banking_application.service.impl;

import com.bank.banking_application.config.JwtTokenProvider;
import com.bank.banking_application.dto.request.LoginRequest;
import com.bank.banking_application.dto.request.UserRequest;
import com.bank.banking_application.dto.response.BankResponse;
import com.bank.banking_application.dto.response.EmailDetails;
import com.bank.banking_application.entity.User;
import com.bank.banking_application.repository.UserRepository;
import com.bank.banking_application.service.interfaces.EmailService;
import com.bank.banking_application.utils.AccountUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private EmailService emailService;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl underTest;

    @Test
    void login() {
        LoginRequest loginRequest = new LoginRequest(
                "test@gmail.com",
                "password"
        );
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt");

        BankResponse bankResponse = underTest.login(loginRequest);

        assertThat(bankResponse.getResponseCode()).isEqualTo(HttpStatus.OK);
        assertThat(bankResponse.getResponseMessage()).isEqualTo("jwt");

        verify(emailService,  times(1)).sendEmailAlert(any(EmailDetails.class));
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void register() {

        UserRequest userRequest = new UserRequest();
        userRequest.setFirstName("firstName");
        userRequest.setLastName("lastName");
        userRequest.setGender("gender");
        userRequest.setAddress("address");
        userRequest.setStateOfOrigin("stateOfOrigin");
        userRequest.setEmail("test@gmail.com");
        userRequest.setPassword("password");

        when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(passwordEncoder.encode(userRequest.getPassword())).thenReturn("encodedPassword");

        User savedUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber("2025123456")
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .password("encodedPassword")
                .phoneNumber(userRequest.getPhoneNumber())
                .status("ACTIVE")
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");

        BankResponse response = underTest.register(userRequest);

        assertThat(response.getResponseCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getResponseMessage()).isEqualTo("jwt-token");
        assertThat(response.getAccountInfo().getAccountNumber()).isEqualTo("2025123456");

        verify(userRepository, times(1)).existsByEmail(userRequest.getEmail());
        verify(userRepository, atLeastOnce()).existsByAccountNumber(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendEmailAlert(any(EmailDetails.class));
    }

    @Test
    void throwIllegalArgumentException(){
        UserRequest request = new UserRequest();
        request.setEmail("existing@email.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> underTest.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE);

        verify(userRepository, times(1)).existsByEmail(request.getEmail());
    }
}