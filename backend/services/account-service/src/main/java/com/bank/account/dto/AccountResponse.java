package com.bank.account.dto;

import com.bank.account.enums.AccountStatus;
import com.bank.account.enums.AccountType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountResponse {

    private String accountNumber;

    private Long customerId;

    private AccountType accountType;

    private AccountStatus status;

    private BigDecimal ledgerBalance;

    private BigDecimal availableBalance;

    private BigDecimal holdBalance;

    private String currency;

    private String ifscCode;

    private String branchCode;
}