package com.bank.account.transaction.repository;

import com.bank.account.transaction.entity.BankTransaction;
import com.bank.account.transaction.enums.TransactionType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM BankTransaction t
            WHERE t.accountNumber = :accountNumber
            AND t.transactionType = :transactionType
            AND t.createdAt BETWEEN :start AND :end
            """)
    BigDecimal getTodayTransactionTotal(
            @Param("accountNumber")
            String accountNumber,

            @Param("transactionType")
            TransactionType transactionType,

            @Param("start")
            LocalDateTime start,

            @Param("end")
            LocalDateTime end
    );
}