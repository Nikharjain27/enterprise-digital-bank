package com.bank.account.service.impl;

import com.bank.account.dto.AccountResponse;
import com.bank.account.dto.CreateAccountRequest;
import com.bank.account.entity.Account;
import com.bank.account.enums.AccountStatus;
import com.bank.account.exception.BaseException;
import com.bank.account.idempotency.entity.IdempotencyRecord;
import com.bank.account.idempotency.repository.IdempotencyRepository;
import com.bank.account.repository.AccountRepository;
import com.bank.account.service.AccountService;
import com.bank.account.transaction.dto.TransactionRequest;
import com.bank.account.transaction.dto.TransactionResponse;
import com.bank.account.transaction.dto.TransferRequest;
import com.bank.account.transaction.entity.BankTransaction;
import com.bank.account.transaction.enums.TransactionType;
import com.bank.account.transaction.repository.BankTransactionRepository;
import com.bank.account.util.AccountNumberGenerator;
import com.bank.account.util.IfscGenerator;
import com.bank.account.util.TransactionReferenceGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final BankTransactionRepository
            bankTransactionRepository;

    private final AccountNumberGenerator
            accountNumberGenerator;

    private final IfscGenerator ifscGenerator;

    private final TransactionReferenceGenerator
            transactionReferenceGenerator;

    private final IdempotencyRepository
            idempotencyRepository;

    private void validateAccountActive(
            Account account
    ) {

        if (
                account.getStatus()
                        == AccountStatus.FROZEN
        ) {

            throw new BaseException(
                    "ACCOUNT_FROZEN",
                    "Account is frozen"
            );
        }

        if (
                account.getStatus()
                        == AccountStatus.CLOSED
        ) {

            throw new BaseException(
                    "ACCOUNT_CLOSED",
                    "Account is closed"
            );
        }

        if (
                account.getStatus()
                        == AccountStatus.DORMANT
        ) {

            throw new BaseException(
                    "ACCOUNT_DORMANT",
                    "Account is dormant"
            );
        }
    }

    @Override
    public AccountResponse createAccount(
            CreateAccountRequest request
    ) {

        String accountNumber =
                accountNumberGenerator
                        .generateAccountNumber();

        while (
                accountRepository.existsByAccountNumber(
                        accountNumber
                )
        ) {

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
                .ifscCode(
                        ifscGenerator.generateIfscCode()
                )
                .branchCode(
                        ifscGenerator.generateBranchCode()
                )
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Account savedAccount =
                accountRepository.save(account);

        return AccountResponse.builder()
                .accountNumber(
                        savedAccount.getAccountNumber()
                )
                .customerId(
                        savedAccount.getCustomerId()
                )
                .accountType(
                        savedAccount.getAccountType()
                )
                .status(
                        savedAccount.getStatus()
                )
                .ledgerBalance(
                        savedAccount.getLedgerBalance()
                )
                .availableBalance(
                        savedAccount.getAvailableBalance()
                )
                .holdBalance(
                        savedAccount.getHoldBalance()
                )
                .currency(
                        savedAccount.getCurrency()
                )
                .ifscCode(
                        savedAccount.getIfscCode()
                )
                .branchCode(
                        savedAccount.getBranchCode()
                )
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
                                new BaseException(
                                        "ACCOUNT_NOT_FOUND",
                                        "Account not found"
                                )
                        );

        validateAccountActive(account);

        account.setLedgerBalance(
                account.getLedgerBalance()
                        .add(request.getAmount())
        );

        account.setAvailableBalance(
                account.getAvailableBalance()
                        .add(request.getAmount())
        );

        account.setUpdatedAt(
                LocalDateTime.now()
        );

        accountRepository.save(account);

        String reference =
                transactionReferenceGenerator
                        .generateReference();

        BankTransaction transaction =
                BankTransaction.builder()
                        .accountNumber(
                                account.getAccountNumber()
                        )
                        .transactionType(
                                TransactionType.DEPOSIT
                        )
                        .amount(request.getAmount())
                        .referenceNumber(reference)
                        .description(
                                request.getDescription()
                        )
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        bankTransactionRepository.save(transaction);

        return TransactionResponse.builder()
                .referenceNumber(reference)
                .accountNumber(
                        account.getAccountNumber()
                )
                .transactionType(
                        TransactionType.DEPOSIT
                )
                .amount(request.getAmount())
                .updatedBalance(
                        account.getAvailableBalance()
                )
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

        validateAccountActive(account);

        if (
                account.getAvailableBalance()
                        .compareTo(
                                request.getAmount()
                        ) < 0
        ) {

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

        account.setUpdatedAt(
                LocalDateTime.now()
        );

        accountRepository.save(account);

        String reference =
                transactionReferenceGenerator
                        .generateReference();

        BankTransaction transaction =
                BankTransaction.builder()
                        .accountNumber(
                                account.getAccountNumber()
                        )
                        .transactionType(
                                TransactionType.WITHDRAWAL
                        )
                        .amount(request.getAmount())
                        .referenceNumber(reference)
                        .description(
                                request.getDescription()
                        )
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        bankTransactionRepository.save(transaction);

        return TransactionResponse.builder()
                .referenceNumber(reference)
                .accountNumber(
                        account.getAccountNumber()
                )
                .transactionType(
                        TransactionType.WITHDRAWAL
                )
                .amount(request.getAmount())
                .updatedBalance(
                        account.getAvailableBalance()
                )
                .message("Withdrawal successful")
                .build();
    }

    @Override
    @Transactional
    public TransactionResponse transfer(
            TransferRequest request,
            String idempotencyKey
    ) {

        if (
                idempotencyKey == null
                        || idempotencyKey.isBlank()
        ) {

            throw new BaseException(
                    "MISSING_IDEMPOTENCY_KEY",
                    "Idempotency key is required"
            );
        }

        IdempotencyRecord existingRecord =
                idempotencyRepository
                        .findByIdempotencyKey(
                                idempotencyKey
                        )
                        .orElse(null);

        if (existingRecord != null) {

            return TransactionResponse.builder()
                    .referenceNumber(
                            existingRecord
                                    .getResponseReference()
                    )
                    .message(
                            "Duplicate request prevented"
                    )
                    .build();
        }

        if (
                request.getFromAccount()
                        .equals(
                                request.getToAccount()
                        )
        ) {

            throw new BaseException(
                    "INVALID_TRANSFER",
                    "Cannot transfer to same account"
            );
        }

        String firstLock =
                request.getFromAccount()
                        .compareTo(
                                request.getToAccount()
                        ) < 0
                        ? request.getFromAccount()
                        : request.getToAccount();

        String secondLock =
                request.getFromAccount()
                        .compareTo(
                                request.getToAccount()
                        ) < 0
                        ? request.getToAccount()
                        : request.getFromAccount();

        Account firstAccount =
                accountRepository
                        .findByAccountNumberForUpdate(
                                firstLock
                        )
                        .orElseThrow(() ->
                                new BaseException(
                                        "ACCOUNT_NOT_FOUND",
                                        "Account not found"
                                )
                        );

        Account secondAccount =
                accountRepository
                        .findByAccountNumberForUpdate(
                                secondLock
                        )
                        .orElseThrow(() ->
                                new BaseException(
                                        "ACCOUNT_NOT_FOUND",
                                        "Account not found"
                                )
                        );

        Account sourceAccount =
                firstAccount.getAccountNumber()
                        .equals(
                                request.getFromAccount()
                        )
                        ? firstAccount
                        : secondAccount;

        Account destinationAccount =
                firstAccount.getAccountNumber()
                        .equals(
                                request.getToAccount()
                        )
                        ? firstAccount
                        : secondAccount;

        validateAccountActive(sourceAccount);

        validateAccountActive(destinationAccount);

        if (
                sourceAccount.getAvailableBalance()
                        .compareTo(
                                request.getAmount()
                        ) < 0
        ) {

            throw new BaseException(
                    "INSUFFICIENT_BALANCE",
                    "Insufficient balance"
            );
        }

        sourceAccount.setLedgerBalance(
                sourceAccount.getLedgerBalance()
                        .subtract(request.getAmount())
        );

        sourceAccount.setAvailableBalance(
                sourceAccount.getAvailableBalance()
                        .subtract(request.getAmount())
        );

        destinationAccount.setLedgerBalance(
                destinationAccount.getLedgerBalance()
                        .add(request.getAmount())
        );

        destinationAccount.setAvailableBalance(
                destinationAccount.getAvailableBalance()
                        .add(request.getAmount())
        );

        sourceAccount.setUpdatedAt(
                LocalDateTime.now()
        );

        destinationAccount.setUpdatedAt(
                LocalDateTime.now()
        );

        accountRepository.save(sourceAccount);

        accountRepository.save(destinationAccount);

        String reference =
                transactionReferenceGenerator
                        .generateReference();

        BankTransaction debitTransaction =
                BankTransaction.builder()
                        .accountNumber(
                                sourceAccount.getAccountNumber()
                        )
                        .transactionType(
                                TransactionType.TRANSFER
                        )
                        .amount(request.getAmount())
                        .referenceNumber(reference)
                        .description(
                                "Transfer to "
                                        + destinationAccount
                                        .getAccountNumber()
                        )
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        BankTransaction creditTransaction =
                BankTransaction.builder()
                        .accountNumber(
                                destinationAccount
                                        .getAccountNumber()
                        )
                        .transactionType(
                                TransactionType.TRANSFER
                        )
                        .amount(request.getAmount())
                        .referenceNumber(reference)
                        .description(
                                "Transfer from "
                                        + sourceAccount
                                        .getAccountNumber()
                        )
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        bankTransactionRepository
                .save(debitTransaction);

        bankTransactionRepository
                .save(creditTransaction);

        IdempotencyRecord record =
                IdempotencyRecord.builder()
                        .idempotencyKey(
                                idempotencyKey
                        )
                        .responseReference(reference)
                        .createdAt(LocalDateTime.now())
                        .build();

        idempotencyRepository.save(record);

        return TransactionResponse.builder()
                .referenceNumber(reference)
                .accountNumber(
                        sourceAccount.getAccountNumber()
                )
                .transactionType(
                        TransactionType.TRANSFER
                )
                .amount(request.getAmount())
                .updatedBalance(
                        sourceAccount
                                .getAvailableBalance()
                )
                .message("Transfer successful")
                .build();
    }

    @Override
    @Transactional
    public void freezeAccount(
            String accountNumber
    ) {

        Account account =
                accountRepository
                        .findByAccountNumberForUpdate(
                                accountNumber
                        )
                        .orElseThrow(() ->
                                new BaseException(
                                        "ACCOUNT_NOT_FOUND",
                                        "Account not found"
                                )
                        );

        account.setStatus(
                AccountStatus.FROZEN
        );

        account.setUpdatedAt(
                LocalDateTime.now()
        );

        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void unfreezeAccount(
            String accountNumber
    ) {

        Account account =
                accountRepository
                        .findByAccountNumberForUpdate(
                                accountNumber
                        )
                        .orElseThrow(() ->
                                new BaseException(
                                        "ACCOUNT_NOT_FOUND",
                                        "Account not found"
                                )
                        );

        account.setStatus(
                AccountStatus.ACTIVE
        );

        account.setUpdatedAt(
                LocalDateTime.now()
        );

        accountRepository.save(account);
    }
}