package com.bank.account.transaction.repository;

import com.bank.account.transaction.entity.BankTransaction;
import com.bank.account.transaction.enums.TransactionType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankTransactionRepository
        extends JpaRepository<BankTransaction, Long> {

    List<BankTransaction>
    findByAccountNumberOrderByCreatedAtDesc(
            String accountNumber
    );

    List<BankTransaction>
    findByAccountNumberAndTransactionTypeOrderByCreatedAtDesc(
            String accountNumber,
            TransactionType transactionType
    );

    List<BankTransaction>
    findByAccountNumberOrderByCreatedAtDesc(
            String accountNumber,
            Pageable pageable
    );
}