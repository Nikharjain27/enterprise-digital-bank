package com.bank.account.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AccountNumberGenerator {

    public String generateAccountNumber() {

        Random random = new Random();

        long number =
                100000000000L
                        + (long)(random.nextDouble()
                        * 900000000000L);

        return String.valueOf(number);
    }
}