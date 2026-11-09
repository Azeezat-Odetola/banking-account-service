package com.azeezat.account.service.controller;

import com.azeezat.account.service.dto.CreateRequest;
import com.azeezat.account.service.entity.Account;
import com.azeezat.account.service.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {

private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;

    }

    @PostMapping
    public Account createAccount(@RequestBody @Valid CreateRequest createRequest) {
        return accountService.createAccount(createRequest);
    }

    @GetMapping("/customer/{customerId}")
    public List<Account> getAccountDetailsByUserId(@PathVariable Long customerId){
        return  accountService.getAccountDetailsByUserId(customerId);
    }

    @GetMapping("/{id}")
    public Account getAccountById(@PathVariable Long id){
        return accountService.getAccountById(id);
    }

    @PatchMapping("/{accountNumber}/close")
    public ResponseEntity<Void>closeAccount(@PathVariable String accountNumber){
        accountService.closeAccount(accountNumber);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/account-number/{accountNumber}")
    public Account getDetailsByAccountNumber(@PathVariable String accountNumber){
        return accountService.getDetailsByAccountNumber(accountNumber);
    }
}
