package com.bank.banking_application.service.impl;

import com.bank.banking_application.entity.User;
import com.bank.banking_application.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    private CustomUserDetailsService underTest;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp(){
        underTest = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername() {

        String email = "test@gmail.com";
        String password = "password";

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = underTest.loadUserByUsername(email);

        assertThat(userDetails).isEqualTo(user);
    }
}