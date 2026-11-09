package com.azeezat.account.service.service;

import com.azeezat.account.service.dto.CreateRequest;
import com.azeezat.account.service.entity.Account;
import com.azeezat.account.service.entity.Status;
import com.azeezat.account.service.exception.ResourceNotFoundException;
import com.azeezat.account.service.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    @Override
    public Account createAccount(CreateRequest request) {

        Account account = new Account();
            account.setAccountName(request.getAccountName());
            account.setCurrency(request.getCurrency());
            account.setStatus(Status.ACTIVE);
            account.setCustomerId(request.getCustomerId());
            account.setAccountType(request.getAccountType());
            account.setAccountBalance(BigDecimal.ZERO);

          return   accountRepository.save(account);
    }


    @Override
    public List<Account> getAccountDetailsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }


    @Override
    public Account getAccountById(Long id) {
         return accountRepository.findById(id)
                 .orElseThrow(()-> new ResourceNotFoundException("Account not found"));

    }


    @Override
    public void closeAccount(String accountNumber) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account does not exist"));

        account.setStatus(Status.CLOSED);

        accountRepository.save(account);
    }


    @Override
    public Account getDetailsByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new ResourceNotFoundException("Account not found"));
    }


}
