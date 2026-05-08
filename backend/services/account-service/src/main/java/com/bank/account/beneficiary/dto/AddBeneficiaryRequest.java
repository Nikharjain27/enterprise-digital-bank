package com.bank.account.beneficiary.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddBeneficiaryRequest {

    @NotBlank
    private String customerAccount;

    @NotBlank
    private String beneficiaryAccount;

    @NotBlank
    private String beneficiaryName;

    @NotBlank
    private String ifscCode;
}