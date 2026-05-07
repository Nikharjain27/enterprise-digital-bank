package com.bank.account.service;

import com.bank.account.dto.AccountResponse;
import com.bank.account.dto.CreateAccountRequest;
import com.bank.account.transaction.dto.TransactionRequest;
import com.bank.account.transaction.dto.TransactionResponse;

public interface AccountService {

    AccountResponse createAccount(
            CreateAccountRequest request
    );

    TransactionResponse deposit(
            TransactionRequest request
    );

    TransactionResponse withdraw(
            TransactionRequest request
    );
}