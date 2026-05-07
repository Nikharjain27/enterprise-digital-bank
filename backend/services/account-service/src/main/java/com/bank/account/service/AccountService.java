package com.bank.account.service;

import com.bank.account.dto.AccountResponse;
import com.bank.account.dto.CreateAccountRequest;

public interface AccountService {

    AccountResponse createAccount(
            CreateAccountRequest request
    );
}