package com.bank.banking_application.service.interfaces;

import com.bank.banking_application.dto.response.BankResponse;
import com.bank.banking_application.dto.request.LoginRequest;
import com.bank.banking_application.dto.request.UserRequest;

public interface AuthService {

    public BankResponse login(LoginRequest loginRequest);
    public BankResponse register(UserRequest userRequest);
}
