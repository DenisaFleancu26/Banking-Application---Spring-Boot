package com.bank.banking_application.service;

import com.bank.banking_application.dto.BankResponse;
import com.bank.banking_application.dto.UserRequest;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);
}
