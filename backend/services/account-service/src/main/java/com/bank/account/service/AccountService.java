package com.bank.account.service;

import com.bank.account.dto.AccountResponse;
import com.bank.account.dto.CreateAccountRequest;
import com.bank.account.transaction.dto.TransactionRequest;
import com.bank.account.transaction.dto.TransactionResponse;
import com.bank.account.transaction.dto.TransferRequest;

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

    TransactionResponse transfer(
            TransferRequest request,
            String idempotencyKey
    );
}