package com.azeezat.account.service.service;

import com.azeezat.account.service.dto.CreateRequest;
import com.azeezat.account.service.entity.Account;

import java.util.List;

public interface AccountService  {
     Account createAccount(CreateRequest request);
     List<Account> getAccountDetailsByUserId(Long userId);
     Account getAccountById(Long id);
     void closeAccount(String accountNumber);
     Account getDetailsByAccountNumber(String accountNumber);
}
