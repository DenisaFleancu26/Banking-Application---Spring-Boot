package com.bank.banking_application.repository;

import com.bank.banking_application.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void checkIfUserExistsByEmail() {

        User user = createUser();
        String email = "denisafleancu@gmail.com";

        underTest.save(user);
        boolean exists = underTest.existsByEmail(email);
        assertThat(exists).isTrue();
    }

    @Test
    void findUserByEmail() {

        User user = createUser();
        String accountNumber = "2025123456";
        String email = "denisafleancu@gmail.com";

        underTest.save(user);
        Optional<User> foundUser = underTest.findByEmail(email);
        assertThat(foundUser)
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u.getAccountNumber()).isEqualTo(accountNumber));
    }

    @Test
    void checkIfUserExistsByAccountNumber() {

        User user = createUser();
        String accountNumber = "2025123456";

        underTest.save(user);
        boolean exists = underTest.existsByAccountNumber(accountNumber);
        assertThat(exists).isTrue();
    }

    @Test
    void findByAccountNumber() {

        User user = createUser();
        String accountNumber = "2025123456";
        String email = "denisafleancu@gmail.com";

        underTest.save(user);
        Optional<User> foundUser = underTest.findByAccountNumber(accountNumber);
        assertThat(foundUser)
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u.getEmail()).isEqualTo(email));
    }

    private User createUser() {
        return User.builder()
                .firstName("Denisa")
                .lastName( "Fleancu")
                .gender("Female")
                .address("Targu-Jiu")
                .stateOfOrigin("Romania")
                .accountBalance(new BigDecimal("1000.00"))
                .accountNumber( "2025123456")
                .email("denisafleancu@gmail.com")
                .password("password")
                .status("ACTIVE")
                .phoneNumber("0765654345")
                .build();
    }
}