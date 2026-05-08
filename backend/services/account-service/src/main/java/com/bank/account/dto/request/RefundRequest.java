package com.bank.account.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class RefundRequest {

    @NotBlank
    private String referenceNumber;

    @NotBlank
    private String sourceAccount;

    @NotNull
    @DecimalMin("1.00")
    private BigDecimal refundAmount;

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(
            String referenceNumber
    ) {
        this.referenceNumber =
                referenceNumber;
    }

    public String getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(
            String sourceAccount
    ) {
        this.sourceAccount =
                sourceAccount;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(
            BigDecimal refundAmount
    ) {
        this.refundAmount =
                refundAmount;
    }
}