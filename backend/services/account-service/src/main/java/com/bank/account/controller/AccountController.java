package com.bank.account.controller;

import com.bank.account.beneficiary.dto.AddBeneficiaryRequest;
import com.bank.account.beneficiary.dto.BeneficiaryResponse;
import com.bank.account.dto.AccountResponse;
import com.bank.account.dto.CreateAccountRequest;
import com.bank.account.service.AccountService;
import com.bank.account.transaction.dto.StatementResponse;
import com.bank.account.transaction.dto.TransactionRequest;
import com.bank.account.transaction.dto.TransactionResponse;
import com.bank.account.transaction.dto.TransferRequest;
import com.bank.account.transaction.enums.TransactionType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request
    ) {

        return new ResponseEntity<>(
                accountService.createAccount(request),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @Valid @RequestBody TransactionRequest request
    ) {

        return ResponseEntity.ok(
                accountService.deposit(request)
        );
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @Valid @RequestBody TransactionRequest request
    ) {

        return ResponseEntity.ok(
                accountService.withdraw(request)
        );
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            @RequestHeader(name = "X-Idempotency-Key")
            String idempotencyKey
    ) {

        return ResponseEntity.ok(
                accountService.transfer(
                        request,
                        idempotencyKey
                )
        );
    }

    @PutMapping("/{accountNumber}/freeze")
    public ResponseEntity<String> freezeAccount(
            @PathVariable("accountNumber")
            String accountNumber
    ) {

        accountService.freezeAccount(accountNumber);

        return ResponseEntity.ok(
                "Account frozen successfully"
        );
    }

    @PutMapping("/{accountNumber}/unfreeze")
    public ResponseEntity<String> unfreezeAccount(
            @PathVariable("accountNumber")
            String accountNumber
    ) {

        accountService.unfreezeAccount(accountNumber);

        return ResponseEntity.ok(
                "Account unfrozen successfully"
        );
    }

    @GetMapping("/{accountNumber}/statement")
    public ResponseEntity<List<StatementResponse>>
    getStatement(
            @PathVariable("accountNumber")
            String accountNumber
    ) {

        return ResponseEntity.ok(
                accountService.getStatement(
                        accountNumber
                )
        );
    }

    @GetMapping("/{accountNumber}/mini-statement")
    public ResponseEntity<List<StatementResponse>>
    getMiniStatement(
            @PathVariable("accountNumber")
            String accountNumber
    ) {

        return ResponseEntity.ok(
                accountService.getMiniStatement(
                        accountNumber
                )
        );
    }

    @GetMapping("/{accountNumber}/statement/type/{type}")
    public ResponseEntity<List<StatementResponse>>
    getStatementByType(
            @PathVariable("accountNumber")
            String accountNumber,

            @PathVariable("type")
            TransactionType type
    ) {

        return ResponseEntity.ok(
                accountService.getStatementByType(
                        accountNumber,
                        type
                )
        );
    }

    @PostMapping("/beneficiaries")
    public ResponseEntity<BeneficiaryResponse>
    addBeneficiary(
            @Valid
            @RequestBody
            AddBeneficiaryRequest request
    ) {

        return new ResponseEntity<>(
                accountService.addBeneficiary(request),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{accountNumber}/beneficiaries")
    public ResponseEntity<List<BeneficiaryResponse>>
    getBeneficiaries(
            @PathVariable("accountNumber")
            String accountNumber
    ) {

        return ResponseEntity.ok(
                accountService.getBeneficiaries(
                        accountNumber
                )
        );
    }
}