package com.azeezat.account.service.controller;

import com.azeezat.account.service.dto.CreateRequest;
import com.azeezat.account.service.entity.Account;
import com.azeezat.account.service.entity.AccountType;
import com.azeezat.account.service.exception.InvalidAccountOperationException;
import com.azeezat.account.service.exception.ResourceNotFoundException;
import com.azeezat.account.service.service.AccountService;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.Mockito.when;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;


    @Test
    @WithMockUser
    void shouldCreateAccountSuccessfully() throws Exception {

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("1234567890");
        account.setAccountName("Gift Philip");
        account.setCurrency("NGN");
        account.setCustomerId(1L);
        account.setAccountType(AccountType.SAVINGS);

        when(accountService.createAccount(any(CreateRequest.class)))
                .thenReturn(account);

        mockMvc.perform(post("/api/account")
                        .with(csrf())
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("""
                        {
                        
                            "accountName": "Gift Philip",
                            "currency": "NGN",
                            "customerId": 1,
                            "accountType": "SAVINGS"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andExpect(jsonPath("$.accountName").value("Gift Philip"));
    }


    @Test
    @WithMockUser
    void shouldGetAccountByIdSuccessfully() throws Exception {

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("1234567890");
        account.setAccountName("Gift Philip");
        account.setCurrency("NGN");
        account.setCustomerId(1L);
        account.setAccountType(AccountType.SAVINGS);

        when(accountService.getAccountById(1L))
                .thenReturn(account);

        mockMvc.perform(get("/api/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andExpect(jsonPath("$.accountName").value("Gift Philip"));
    }


    @Test
    @WithMockUser
    void shouldGetAccountsByCustomerIdSuccessfully() throws Exception {

        Account firstAccount = new Account();
        firstAccount.setId(1L);
        firstAccount.setAccountNumber("1234567890");
        firstAccount.setAccountName("Gift Philip");
        firstAccount.setCurrency("NGN");
        firstAccount.setCustomerId(1L);
        firstAccount.setAccountType(AccountType.SAVINGS);

        Account secondAccount = new Account();
        secondAccount.setId(2L);
        secondAccount.setAccountNumber("9876543210");
        secondAccount.setAccountName("Gift Philip");
        secondAccount.setCurrency("NGN");
        secondAccount.setCustomerId(1L);
        secondAccount.setAccountType(AccountType.CURRENT);

        when(accountService.getAccountDetailsByCustomerId(1L))
                .thenReturn(List.of(firstAccount, secondAccount));

        mockMvc.perform(get("/api/account/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].accountNumber").value("1234567890"))
                .andExpect(jsonPath("$[1].accountNumber").value("9876543210"));
    }


    @Test
    @WithMockUser
    void shouldCloseAccountSuccessfully() throws Exception {

        doNothing().when(accountService).closeAccount("1234567890");

        mockMvc.perform(patch("/api/account/1234567890/close")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(accountService).closeAccount("1234567890");
    }


    @Test
    @WithMockUser
    void shouldGetAccountByAccountNumberSuccessfully() throws Exception {

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("1234567890");
        account.setAccountName("Gift Philip");
        account.setCurrency("NGN");
        account.setCustomerId(1L);
        account.setAccountType(AccountType.SAVINGS);

        when(accountService.getDetailsByAccountNumber("1234567890"))
                .thenReturn(account);

        mockMvc.perform(get("/api/account/account-number/1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andExpect(jsonPath("$.accountName").value("Gift Philip"))
                .andExpect(jsonPath("$.currency").value("NGN"));
    }


    @Test
    @WithMockUser
    void shouldRejectAccountCreationWhenAccountNameIsBlank() throws Exception {

        mockMvc.perform(post("/api/account")
                        .with(csrf())
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("""
                            {
                                "accountName": "",
                                "currency": "NGN",
                                "customerId": 1,
                                "accountType": "SAVINGS"
                            }
                            """))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).createAccount(any(CreateRequest.class));
    }


    @Test
    @WithMockUser
    void shouldRejectAccountCreationWhenCurrencyIsBlank() throws Exception {

        mockMvc.perform(post("/api/account")
                        .with(csrf())
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("""
                            {
                                "accountName": "Gift Philip",
                                "currency": "",
                                "customerId": 1,
                                "accountType": "SAVINGS"
                            }
                            """))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).createAccount(any(CreateRequest.class));
    }


    @Test
    @WithMockUser
    void shouldRejectAccountCreationWhenCustomerIdIsZero() throws Exception {

        mockMvc.perform(post("/api/account")
                        .with(csrf())
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("""
                            {
                                "accountName": "Gift Philip",
                                "currency": "NGN",
                                "customerId": 0,
                                "accountType": "SAVINGS"
                            }
                            """))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).createAccount(any(CreateRequest.class));
    }

    @Test
    @WithMockUser
    void shouldRejectAccountCreationWhenAccountTypeIsMissing() throws Exception {

        mockMvc.perform(post("/api/account")
                        .with(csrf())
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("""
                            {
                                "accountName": "Gift Philip",
                                "currency": "NGN",
                                "customerId": 1
                            }
                            """))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).createAccount(any(CreateRequest.class));
    }



    @Test
    @WithMockUser
    void shouldReturnEmptyListWhenCustomerHasNoAccounts() throws Exception {

        when(accountService.getAccountDetailsByCustomerId(1L))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/account/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenAccountDoesNotExist() throws Exception {

        when(accountService.getAccountById(999L))
                .thenThrow(new ResourceNotFoundException("Account not found"));

        mockMvc.perform(get("/api/account/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Account not found"));
    }

    @Test
    @WithMockUser
    void shouldReturnBadRequestWhenAccountIsAlreadyClosed() throws Exception {

        doThrow(new InvalidAccountOperationException(
                "Account has already been closed."
        )).when(accountService).closeAccount("1234567890");

        mockMvc.perform(patch("/api/account/1234567890/close")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Account has already been closed."));
    }


    @Test
    @WithMockUser
    void shouldReturnValidationErrorMessageWhenAccountNameIsBlank() throws Exception {

        mockMvc.perform(post("/api/account")
                        .with(csrf())
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("""
                            {
                                "accountName": "",
                                "currency": "NGN",
                                "customerId": 1,
                                "accountType": "SAVINGS"
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accountName")
                        .value("Account name is required!"));

        verify(accountService, never()).createAccount(any(CreateRequest.class));
    }



}
