package com.bank.account.transaction.entity;

import com.bank.account.entity.BaseEntity;
import com.bank.account.enums.TransactionStatus;
import com.bank.account.transaction.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "bank_transactions")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BankTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number",
            nullable = false)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type",
            nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false,
            precision = 19,
            scale = 2)
    private BigDecimal amount;

    @Column(name = "reference_number",
            nullable = false)
    private String referenceNumber;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status =
            TransactionStatus.SUCCESS;

    @Column(name = "original_transaction_reference")
    private String originalTransactionReference;

    @Column(
            name = "reversal_transaction",
            nullable = false
    )
    private Boolean reversalTransaction = false;
}