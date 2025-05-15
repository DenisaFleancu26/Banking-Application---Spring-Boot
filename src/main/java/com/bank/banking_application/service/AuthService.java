package com.bank.banking_application.service;

import com.bank.banking_application.dto.BankResponse;
import com.bank.banking_application.dto.LoginDto;
import com.bank.banking_application.dto.UserRequest;

public interface AuthService {

    public BankResponse login(LoginDto loginDto);
    public BankResponse register(UserRequest userRequest);
}
