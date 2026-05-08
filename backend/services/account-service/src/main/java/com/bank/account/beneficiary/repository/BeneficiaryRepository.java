package com.bank.account.beneficiary.repository;

import com.bank.account.beneficiary.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BeneficiaryRepository
        extends JpaRepository<Beneficiary, Long> {

    List<Beneficiary>
    findByCustomerAccount(
            String customerAccount
    );

    Optional<Beneficiary>
    findByCustomerAccountAndBeneficiaryAccount(
            String customerAccount,
            String beneficiaryAccount
    );
}