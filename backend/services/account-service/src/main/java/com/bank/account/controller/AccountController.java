package com.bank.account.controller;

import com.bank.account.dto.AccountResponse;
import com.bank.account.dto.CreateAccountRequest;
import com.bank.account.service.AccountService;
import com.bank.account.transaction.dto.TransactionRequest;
import com.bank.account.transaction.dto.TransactionResponse;
import com.bank.account.transaction.dto.TransferRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request
    ) {

        return new ResponseEntity<>(
                accountService.createAccount(request),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @Valid @RequestBody TransactionRequest request
    ) {

        return ResponseEntity.ok(
                accountService.deposit(request)
        );
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @Valid @RequestBody TransactionRequest request
    ) {

        return ResponseEntity.ok(
                accountService.withdraw(request)
        );
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            @RequestHeader("X-Idempotency-Key")
            String idempotencyKey
    ) {

        return ResponseEntity.ok(
                accountService.transfer(
                        request,
                        idempotencyKey
                )
        );
    }
}