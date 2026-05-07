package com.bank.account.util;

import org.springframework.stereotype.Component;

@Component
public class IfscGenerator {

    public String generateIfscCode() {

        return "BANK0001234";
    }

    public String generateBranchCode() {

        return "BR001";
    }
}