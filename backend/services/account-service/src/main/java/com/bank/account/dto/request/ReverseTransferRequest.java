package com.bank.account.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ReverseTransferRequest {

    @NotBlank
    private String referenceNumber;

    @NotBlank
    private String sourceAccount;

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
}