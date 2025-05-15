package com.bank.banking_application.service.interfaces;

import com.bank.banking_application.dto.response.BankResponse;
import com.bank.banking_application.dto.LoginDTO;
import com.bank.banking_application.dto.request.UserRequest;

public interface AuthService {

    public BankResponse login(LoginDTO loginDto);
    public BankResponse register(UserRequest userRequest);
}
