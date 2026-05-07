package com.bank.account.transaction.dto;

import com.bank.account.transaction.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransactionResponse {

    private String referenceNumber;

    private String accountNumber;

    private TransactionType transactionType;

    private BigDecimal amount;

    private BigDecimal updatedBalance;

    private String message;
}