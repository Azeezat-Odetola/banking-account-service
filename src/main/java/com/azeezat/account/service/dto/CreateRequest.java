package com.azeezat.account.service.dto;

import com.azeezat.account.service.entity.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateRequest {

    @NotBlank(message = "Account name is required!")
    private String accountName;

    @NotBlank(message = "Currency is required!")
    private String currency;


    @NotNull(message = "Customer ID is required!")
    private Long customerId;

    @NotNull(message = "Account type is required!")
    private AccountType accountType;

    public CreateRequest() {
    }

    public CreateRequest(String accountName, String currency, Long customerId, AccountType accountType) {
        this.accountName = accountName;
        this.currency = currency;
        this.customerId = customerId;
        this.accountType = accountType;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    @Override
    public String toString() {
        return "CreateRequest{" +
                "accountName='" + accountName + '\'' +
                ", currency='" + currency + '\'' +
                ", customerId=" + customerId +
                ", accountType=" + accountType +
                '}';
    }
}