package com.bank.account.service;

import com.bank.account.dto.AccountResponse;
import com.bank.account.dto.CreateAccountRequest;
import com.bank.account.transaction.dto.TransactionRequest;
import com.bank.account.transaction.dto.TransactionResponse;
import com.bank.account.transaction.dto.TransferRequest;
import com.bank.account.transaction.dto.StatementResponse;
import com.bank.account.transaction.enums.TransactionType;

import java.util.List;

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

    void freezeAccount(
            String accountNumber
    );

    void unfreezeAccount(
            String accountNumber
    );

    List<StatementResponse> getStatement(
            String accountNumber
    );

    List<StatementResponse> getMiniStatement(
            String accountNumber
    );

    List<StatementResponse> getStatementByType(
            String accountNumber,
            TransactionType transactionType
    );
}