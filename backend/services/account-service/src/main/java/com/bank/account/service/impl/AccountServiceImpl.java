package com.bank.account.service.impl;

import com.bank.account.beneficiary.dto.AddBeneficiaryRequest;
import com.bank.account.beneficiary.dto.BeneficiaryResponse;
import com.bank.account.beneficiary.entity.Beneficiary;
import com.bank.account.beneficiary.repository.BeneficiaryRepository;
import com.bank.account.dto.AccountResponse;
import com.bank.account.dto.CreateAccountRequest;
import com.bank.account.entity.Account;
import com.bank.account.enums.AccountStatus;
import com.bank.account.exception.BaseException;
import com.bank.account.idempotency.entity.IdempotencyRecord;
import com.bank.account.idempotency.repository.IdempotencyRepository;
import com.bank.account.repository.AccountRepository;
import com.bank.account.service.AccountService;
import com.bank.account.transaction.dto.StatementResponse;
import com.bank.account.transaction.dto.TransactionRequest;
import com.bank.account.transaction.dto.TransactionResponse;
import com.bank.account.transaction.dto.TransferRequest;
import com.bank.account.transaction.entity.BankTransaction;
import com.bank.account.transaction.enums.TransactionType;
import com.bank.account.transaction.repository.BankTransactionRepository;
import com.bank.account.util.AccountNumberGenerator;
import com.bank.account.util.IfscGenerator;
import com.bank.account.util.TransactionReferenceGenerator;
import com.bank.account.dto.request.ReverseTransferRequest;
import com.bank.account.transaction.enums.TransactionStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private static final BigDecimal
            DAILY_TRANSFER_LIMIT =
            BigDecimal.valueOf(10000);

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

    private final BeneficiaryRepository
            beneficiaryRepository;

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

    private void validateDailyTransferLimit(
            String accountNumber,
            BigDecimal transferAmount
    ) {

        LocalDateTime startOfDay =
                LocalDateTime.now()
                        .toLocalDate()
                        .atStartOfDay();

        LocalDateTime endOfDay =
                startOfDay.plusDays(1);

        BigDecimal todayTotal =
                bankTransactionRepository
                        .getTodayTransactionTotal(
                                accountNumber,
                                TransactionType.TRANSFER,
                                startOfDay,
                                endOfDay
                        );

        BigDecimal projectedTotal =
                todayTotal.add(transferAmount);

        if (
                projectedTotal.compareTo(
                        DAILY_TRANSFER_LIMIT
                ) > 0
        ) {

            throw new BaseException(
                    "DAILY_TRANSFER_LIMIT_EXCEEDED",
                    "Daily transfer limit exceeded"
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

        Beneficiary beneficiary =
                beneficiaryRepository
                        .findByCustomerAccountAndBeneficiaryAccount(
                                sourceAccount.getAccountNumber(),
                                destinationAccount.getAccountNumber()
                        )
                        .orElseThrow(() ->
                                new BaseException(
                                        "BENEFICIARY_NOT_FOUND",
                                        "Beneficiary not added"
                                )
                        );

        if (
                !beneficiary.isActive()
        ) {

            if (
                    LocalDateTime.now()
                            .isBefore(
                                    beneficiary
                                            .getActivationTime()
                            )
            ) {

                throw new BaseException(
                        "BENEFICIARY_COOLDOWN",
                        "Beneficiary cooling period active"
                );
            }

            beneficiary.setActive(true);

            beneficiaryRepository.save(beneficiary);
        }

        validateDailyTransferLimit(
                sourceAccount.getAccountNumber(),
                request.getAmount()
        );

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
                        .toAccountNumber(
                                destinationAccount.getAccountNumber()
                        )
                        .transactionType(
                                TransactionType.TRANSFER
                        )
                        .amount(request.getAmount())
                        .referenceNumber(reference)
                        .status(TransactionStatus.SUCCESS)
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

    @Override
    public List<StatementResponse> getStatement(
            String accountNumber
    ) {

        List<BankTransaction> transactions =
                bankTransactionRepository
                        .findByAccountNumberOrderByCreatedAtDesc(
                                accountNumber
                        );

        return transactions.stream()
                .map(transaction ->
                        StatementResponse.builder()
                                .referenceNumber(
                                        transaction
                                                .getReferenceNumber()
                                )
                                .transactionType(
                                        transaction
                                                .getTransactionType()
                                )
                                .amount(
                                        transaction.getAmount()
                                )
                                .description(
                                        transaction.getDescription()
                                )
                                .transactionTime(
                                        transaction.getCreatedAt()
                                )
                                .build()
                )
                .toList();
    }

    @Override
    public List<StatementResponse> getMiniStatement(
            String accountNumber
    ) {

        List<BankTransaction> transactions =
                bankTransactionRepository
                        .findByAccountNumberOrderByCreatedAtDesc(
                                accountNumber,
                                PageRequest.of(0, 5)
                        );

        return transactions.stream()
                .map(transaction ->
                        StatementResponse.builder()
                                .referenceNumber(
                                        transaction
                                                .getReferenceNumber()
                                )
                                .transactionType(
                                        transaction
                                                .getTransactionType()
                                )
                                .amount(
                                        transaction.getAmount()
                                )
                                .description(
                                        transaction.getDescription()
                                )
                                .transactionTime(
                                        transaction.getCreatedAt()
                                )
                                .build()
                )
                .toList();
    }

    @Override
    public List<StatementResponse> getStatementByType(
            String accountNumber,
            TransactionType transactionType
    ) {

        List<BankTransaction> transactions =
                bankTransactionRepository
                        .findByAccountNumberAndTransactionTypeOrderByCreatedAtDesc(
                                accountNumber,
                                transactionType
                        );

        return transactions.stream()
                .map(transaction ->
                        StatementResponse.builder()
                                .referenceNumber(
                                        transaction
                                                .getReferenceNumber()
                                )
                                .transactionType(
                                        transaction
                                                .getTransactionType()
                                )
                                .amount(
                                        transaction.getAmount()
                                )
                                .description(
                                        transaction.getDescription()
                                )
                                .transactionTime(
                                        transaction.getCreatedAt()
                                )
                                .build()
                )
                .toList();
    }

    @Override
    @Transactional
    public BeneficiaryResponse addBeneficiary(
            AddBeneficiaryRequest request
    ) {

        if (
                request.getCustomerAccount()
                        .equals(
                                request.getBeneficiaryAccount()
                        )
        ) {

            throw new BaseException(
                    "INVALID_BENEFICIARY",
                    "Cannot add same account"
            );
        }

        beneficiaryRepository
                .findByCustomerAccountAndBeneficiaryAccount(
                        request.getCustomerAccount(),
                        request.getBeneficiaryAccount()
                )
                .ifPresent(existing -> {

                    throw new BaseException(
                            "BENEFICIARY_EXISTS",
                            "Beneficiary already exists"
                    );
                });

        Beneficiary beneficiary =
                Beneficiary.builder()
                        .customerAccount(
                                request.getCustomerAccount()
                        )
                        .beneficiaryAccount(
                                request.getBeneficiaryAccount()
                        )
                        .beneficiaryName(
                                request.getBeneficiaryName()
                        )
                        .ifscCode(
                                request.getIfscCode()
                        )
                        .active(false)
                        .activationTime(
                                LocalDateTime.now()
                                        .plusMinutes(30)
                        )
                        .createdAt(LocalDateTime.now())
                        .build();

        beneficiaryRepository.save(beneficiary);

        return BeneficiaryResponse.builder()
                .beneficiaryAccount(
                        beneficiary.getBeneficiaryAccount()
                )
                .beneficiaryName(
                        beneficiary.getBeneficiaryName()
                )
                .ifscCode(
                        beneficiary.getIfscCode()
                )
                .active(
                        beneficiary.isActive()
                )
                .activationTime(
                        beneficiary.getActivationTime()
                )
                .build();
    }

    @Override
    public List<BeneficiaryResponse> getBeneficiaries(
            String customerAccount
    ) {

        return beneficiaryRepository
                .findByCustomerAccount(
                        customerAccount
                )
                .stream()
                .map(beneficiary ->
                        BeneficiaryResponse.builder()
                                .beneficiaryAccount(
                                        beneficiary
                                                .getBeneficiaryAccount()
                                )
                                .beneficiaryName(
                                        beneficiary
                                                .getBeneficiaryName()
                                )
                                .ifscCode(
                                        beneficiary
                                                .getIfscCode()
                                )
                                .active(
                                        beneficiary.isActive()
                                )
                                .activationTime(
                                        beneficiary
                                                .getActivationTime()
                                )
                                .build()
                )
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String reverseTransfer(
            ReverseTransferRequest request
    ) {

        /*
         * FIND ORIGINAL TRANSACTION
         */
        BankTransaction originalTransaction =
                bankTransactionRepository
                        .findByReferenceNumberAndAccountNumber(
                                request.getReferenceNumber(),
                                request.getSourceAccount()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Original transaction not found"
                                )
                        );

        /*
         * ALLOW ONLY TRANSFER REVERSAL
         */
        if (
                originalTransaction.getTransactionType()
                        != TransactionType.TRANSFER
        ) {

            throw new RuntimeException(
                    "Only transfer transactions can be reversed"
            );
        }

        /*
         * PREVENT DOUBLE REVERSAL
         */
        boolean alreadyReversed =
                bankTransactionRepository
                        .existsByOriginalTransactionReference(
                                originalTransaction
                                        .getReferenceNumber()
                        );

        if (alreadyReversed) {

            throw new RuntimeException(
                    "Transaction already reversed"
            );
        }

        /*
         * FIND ACCOUNTS
         */
        Account fromAccount =
                accountRepository
                        .findByAccountNumber(
                                originalTransaction
                                        .getToAccountNumber()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Destination account not found"
                                )
                        );

        Account toAccount =
                accountRepository
                        .findByAccountNumber(
                                originalTransaction
                                        .getAccountNumber()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Source account not found"
                                )
                        );

        /*
         * CHECK BALANCE
         */
        if (
                fromAccount.getAvailableBalance()
                        .compareTo(
                                originalTransaction.getAmount()
                        ) < 0
        ) {

            throw new RuntimeException(
                    "Insufficient balance for reversal"
            );
        }

        /*
         * REVERSE BALANCES
         */
        fromAccount.setLedgerBalance(
                fromAccount.getLedgerBalance()
                        .subtract(
                                originalTransaction.getAmount()
                        )
        );

        fromAccount.setAvailableBalance(
                fromAccount.getAvailableBalance()
                        .subtract(
                                originalTransaction.getAmount()
                        )
        );

        toAccount.setLedgerBalance(
                toAccount.getLedgerBalance()
                        .add(
                                originalTransaction.getAmount()
                        )
        );

        toAccount.setAvailableBalance(
                toAccount.getAvailableBalance()
                        .add(
                                originalTransaction.getAmount()
                        )
        );

        accountRepository.save(fromAccount);

        accountRepository.save(toAccount);

        /*
         * CREATE REVERSAL TRANSACTION
         */
        BankTransaction reversalTransaction =
                new BankTransaction();

        reversalTransaction.setAccountNumber(
                fromAccount.getAccountNumber()
        );

        reversalTransaction.setToAccountNumber(
                toAccount.getAccountNumber()
        );

        reversalTransaction.setAmount(
                originalTransaction.getAmount()
        );

        reversalTransaction.setDescription(
                "REVERSAL : "
                        + originalTransaction
                        .getReferenceNumber()
        );

        reversalTransaction.setTransactionType(
                TransactionType.REVERSAL
        );

        reversalTransaction.setStatus(
                TransactionStatus.SUCCESS
        );

        reversalTransaction.setReferenceNumber(
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 16)
                        .toUpperCase()
        );

        reversalTransaction
                .setOriginalTransactionReference(
                        originalTransaction
                                .getReferenceNumber()
                );

        reversalTransaction
                .setReversalTransaction(true);

        bankTransactionRepository
                .save(reversalTransaction);

        /*
         * UPDATE ORIGINAL STATUS
         */
        originalTransaction.setStatus(
                TransactionStatus.REVERSED
        );

        bankTransactionRepository
                .save(originalTransaction);

        return "Transfer reversed successfully";
    }
}