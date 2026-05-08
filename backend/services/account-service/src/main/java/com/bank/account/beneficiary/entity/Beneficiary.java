package com.bank.account.beneficiary.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "beneficiaries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Beneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_account",
            nullable = false)
    private String customerAccount;

    @Column(name = "beneficiary_account",
            nullable = false)
    private String beneficiaryAccount;

    @Column(name = "beneficiary_name",
            nullable = false)
    private String beneficiaryName;

    @Column(name = "ifsc_code",
            nullable = false)
    private String ifscCode;

    @Column(name = "active",
            nullable = false)
    private boolean active;

    @Column(name = "activation_time")
    private LocalDateTime activationTime;

    @Column(name = "created_at",
            nullable = false)
    private LocalDateTime createdAt;
}