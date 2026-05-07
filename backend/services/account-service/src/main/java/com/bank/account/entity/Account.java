package com.bank.account.entity;

import com.bank.account.enums.AccountStatus;
import com.bank.account.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number",
            nullable = false,
            unique = true)
    private String accountNumber;

    @Column(name = "customer_id",
            nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type",
            nullable = false)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Column(name = "ledger_balance",
            nullable = false,
            precision = 19,
            scale = 2)
    private BigDecimal ledgerBalance;

    @Column(name = "available_balance",
            nullable = false,
            precision = 19,
            scale = 2)
    private BigDecimal availableBalance;

    @Column(name = "hold_balance",
            nullable = false,
            precision = 19,
            scale = 2)
    private BigDecimal holdBalance;

    @Column(name = "currency",
            nullable = false)
    private String currency;

    @Column(name = "ifsc_code",
            nullable = false)
    private String ifscCode;

    @Column(name = "branch_code",
            nullable = false)
    private String branchCode;
}