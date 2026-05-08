package com.bank.account.transaction.dto;

import com.bank.account.transaction.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class StatementResponse {

    private String referenceNumber;

    private TransactionType transactionType;

    private BigDecimal amount;

    private String description;

    private LocalDateTime transactionTime;
}