package com.bank.account.transaction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotBlank(message = "From account required")
    private String fromAccount;

    @NotBlank(message = "To account required")
    private String toAccount;

    @NotNull(message = "Amount required")
    @DecimalMin(value = "0.01",
            message = "Amount must be greater than zero")
    private BigDecimal amount;

    private String description;
}