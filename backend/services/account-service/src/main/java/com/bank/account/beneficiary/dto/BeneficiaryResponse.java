package com.bank.account.beneficiary.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BeneficiaryResponse {

    private String beneficiaryAccount;

    private String beneficiaryName;

    private String ifscCode;

    private boolean active;

    private LocalDateTime activationTime;
}