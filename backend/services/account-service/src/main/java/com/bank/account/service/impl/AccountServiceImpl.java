package com.bank.account.service.impl;

import com.bank.account.dto.AccountResponse;
import com.bank.account.dto.CreateAccountRequest;
import com.bank.account.entity.Account;
import com.bank.account.enums.AccountStatus;
import com.bank.account.repository.AccountRepository;
import com.bank.account.service.AccountService;
import com.bank.account.util.AccountNumberGenerator;
import com.bank.account.util.IfscGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final AccountNumberGenerator accountNumberGenerator;

    private final IfscGenerator ifscGenerator;

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
}