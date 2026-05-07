package com.bank.account.service.impl;

import com.bank.account.dto.AccountResponse;
import com.bank.account.dto.CreateAccountRequest;
import com.bank.account.entity.Account;
import com.bank.account.enums.AccountStatus;
import com.bank.account.exception.BaseException;
import com.bank.account.repository.AccountRepository;
import com.bank.account.service.AccountService;
import com.bank.account.util.AccountNumberGenerator;
import com.bank.account.util.IfscGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.bank.account.transaction.dto.TransactionRequest;
import com.bank.account.transaction.dto.TransactionResponse;
import com.bank.account.transaction.entity.BankTransaction;
import com.bank.account.transaction.enums.TransactionType;
import com.bank.account.transaction.repository.BankTransactionRepository;
import com.bank.account.util.TransactionReferenceGenerator;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final AccountNumberGenerator accountNumberGenerator;

    private final IfscGenerator ifscGenerator;

    private final BankTransactionRepository bankTransactionRepository;

    private final TransactionReferenceGenerator
            transactionReferenceGenerator;

    @Override
    public AccountResponse createAccount(
            CreateAccountRequest request
    ) {

        String accountNumber =
                accountNumberGenerator.generateAccountNumber();

        while (accountRepository.existsByAccountNumber(
                accountNumber
        )) {

            accountNumber =
                    accountNumberGenerator
                            .generateAccountNumber();
        }

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .customerId(request.getCustomerId())
                .accountType(request.getAccountType())
                .status(AccountStatus.ACTIVE)
                .ledgerBalance(BigDecimal.ZERO)
                .availableBalance(BigDecimal.ZERO)
                .holdBalance(BigDecimal.ZERO)
                .currency("INR")
                .ifscCode(ifscGenerator.generateIfscCode())
                .branchCode(ifscGenerator.generateBranchCode())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Account savedAccount =
                accountRepository.save(account);

        return AccountResponse.builder()
                .accountNumber(savedAccount.getAccountNumber())
                .customerId(savedAccount.getCustomerId())
                .accountType(savedAccount.getAccountType())
                .status(savedAccount.getStatus())
                .ledgerBalance(savedAccount.getLedgerBalance())
                .availableBalance(savedAccount.getAvailableBalance())
                .holdBalance(savedAccount.getHoldBalance())
                .currency(savedAccount.getCurrency())
                .ifscCode(savedAccount.getIfscCode())
                .branchCode(savedAccount.getBranchCode())
                .build();
    }

    @Override
    @Transactional
    public TransactionResponse deposit(
            TransactionRequest request
    ) {

        Account account =
                accountRepository
                        .findByAccountNumberForUpdate(
                                request.getAccountNumber()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Account not found"
                                )
                        );

        account.setLedgerBalance(
                account.getLedgerBalance()
                        .add(request.getAmount())
        );

        account.setAvailableBalance(
                account.getAvailableBalance()
                        .add(request.getAmount())
        );

        account.setUpdatedAt(LocalDateTime.now());

        accountRepository.save(account);

        String reference =
                transactionReferenceGenerator
                        .generateReference();

        BankTransaction transaction =
                BankTransaction.builder()
                        .accountNumber(account.getAccountNumber())
                        .transactionType(TransactionType.DEPOSIT)
                        .amount(request.getAmount())
                        .referenceNumber(reference)
                        .description(request.getDescription())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        bankTransactionRepository.save(transaction);

        return TransactionResponse.builder()
                .referenceNumber(reference)
                .accountNumber(account.getAccountNumber())
                .transactionType(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .updatedBalance(account.getAvailableBalance())
                .message("Deposit successful")
                .build();
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(
            TransactionRequest request
    ) {

        Account account =
                accountRepository
                        .findByAccountNumberForUpdate(
                                request.getAccountNumber()
                        )
                        .orElseThrow(() ->
                                new BaseException(
                                        "ACCOUNT_NOT_FOUND",
                                        "Account not found"
                                )
                        );

        if (account.getAvailableBalance()
                .compareTo(request.getAmount()) < 0) {

            throw new BaseException(
                    "INSUFFICIENT_BALANCE",
                    "Insufficient balance"
            );
        }

        account.setLedgerBalance(
                account.getLedgerBalance()
                        .subtract(request.getAmount())
        );

        account.setAvailableBalance(
                account.getAvailableBalance()
                        .subtract(request.getAmount())
        );

        account.setUpdatedAt(LocalDateTime.now());

        accountRepository.save(account);

        String reference =
                transactionReferenceGenerator
                        .generateReference();

        BankTransaction transaction =
                BankTransaction.builder()
                        .accountNumber(account.getAccountNumber())
                        .transactionType(TransactionType.WITHDRAWAL)
                        .amount(request.getAmount())
                        .referenceNumber(reference)
                        .description(request.getDescription())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        bankTransactionRepository.save(transaction);

        return TransactionResponse.builder()
                .referenceNumber(reference)
                .accountNumber(account.getAccountNumber())
                .transactionType(TransactionType.WITHDRAWAL)
                .amount(request.getAmount())
                .updatedBalance(account.getAvailableBalance())
                .message("Withdrawal successful")
                .build();
    }
}