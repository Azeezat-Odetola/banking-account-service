package com.azeezat.account.service.service;

import com.azeezat.account.service.dto.CreateRequest;
import com.azeezat.account.service.entity.Account;
import com.azeezat.account.service.entity.AccountType;
import com.azeezat.account.service.entity.Status;
import com.azeezat.account.service.exception.InvalidAccountOperationException;
import com.azeezat.account.service.exception.ResourceNotFoundException;
import com.azeezat.account.service.repository.AccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Spy
    @InjectMocks
    private AccountServiceImpl accountServiceImpl;

    @Test
    void shouldCreateAccountSuccessful() {
        CreateRequest createRequest = new CreateRequest();

        createRequest.setAccountName("Gift Philip");
        createRequest.setCurrency("$");
        createRequest.setCustomerId(1L);
        createRequest.setAccountType(AccountType.CURRENT);

        when(accountRepository.existsByAccountNumber(anyString()))
                .thenReturn(false);

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Account account = accountServiceImpl.createAccount(createRequest);

        Assertions.assertEquals("Gift Philip", account.getAccountName());
        Assertions.assertEquals("$", account.getCurrency());
        Assertions.assertEquals(1L, account.getCustomerId());
        Assertions.assertEquals(AccountType.CURRENT, account.getAccountType());
        Assertions.assertEquals(BigDecimal.ZERO, account.getAccountBalance());
        Assertions.assertTrue(account.getAccountNumber().matches("\\d{10}"));


        verify(accountRepository).save(any(Account.class));
    }


    @Test
    void shouldReturnAccountsForCustomer(){
        Account account1 = new Account();
        Account account2 = new Account();

        account1.setAccountNumber("1234567890");
        account2.setAccountNumber("0987654321");

        account1.setCustomerId(22L);
        account2.setCustomerId(22L);

        List<Account> accountList = List.of(account1, account2);
        when(accountRepository.findByCustomerId(22L)).thenReturn(accountList);

        List<Account> results = accountServiceImpl.getAccountDetailsByCustomerId(22L);

        Assertions.assertEquals(2, results.size());
        verify(accountRepository).findByCustomerId(22L);

        Assertions.assertEquals(22L, results.get(0).getCustomerId());
        Assertions.assertEquals(22L, results.get(1).getCustomerId());

        Assertions.assertEquals("1234567890", results.get(0).getAccountNumber());
        Assertions.assertEquals("0987654321", results.get(1).getAccountNumber());

    }

    @Test
    void shouldGenerateNewAccountNumberWhenDuplicateExists() {

        CreateRequest createRequest = new CreateRequest();

        createRequest.setAccountName("Gift Philip");
        createRequest.setCurrency("$");
        createRequest.setCustomerId(1L);
        createRequest.setAccountType(AccountType.CURRENT);

        doReturn("1234567890")
                .doReturn("9876543210")
                .when(accountServiceImpl).generateAccountNumber();

        when(accountRepository.existsByAccountNumber(anyString()))
                .thenReturn(true, false);

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Account account = accountServiceImpl.createAccount(createRequest);

        Assertions.assertEquals("9876543210", account.getAccountNumber());

        verify(accountRepository, times(2))
                .existsByAccountNumber(anyString());

        verify(accountRepository).save(any(Account.class));
    }


    @Test
    void shouldReturnAccountWhenIdExists(){
        Account account = new Account();
        account.setId(1L);
        account.setCustomerId(1L);

        when(accountRepository.findById(1L)).thenReturn(java.util.Optional.of(account));

        Account result = accountServiceImpl.getAccountById(1L);

        Assertions.assertEquals(1L, result.getId());
        verify(accountRepository).findById(1L);

    }



    @Test
    void  shouldThrowExceptionWhenAccountIdDoesNotExists(){

        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> accountServiceImpl.getAccountById(1L));

        Assertions.assertEquals("Account not found", exception.getMessage());
        verify(accountRepository).findById(1L);
    }



    @Test
    void shouldCloseAccountSuccessfully(){
        Account account = new Account();
        account.setAccountNumber("1234567890");
        account.setStatus(Status.ACTIVE);
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(java.util.Optional.of(account));
        accountServiceImpl.closeAccount("1234567890");
        Assertions.assertEquals(Status.CLOSED, account.getStatus());
        verify(accountRepository).save(account);
        verify(accountRepository).findByAccountNumber("1234567890");


    }


    @Test
    void shouldThrowExceptionWhenAccountDoesNotExist(){
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, ()-> accountServiceImpl.closeAccount("1234567890"));

        Assertions.assertEquals("Account does not exist", exception.getMessage());
        verify(accountRepository).findByAccountNumber("1234567890");

    }


    @Test
    void shouldThrowExceptionWhenAccountIsAlreadyClosed() {
        Account account = new Account();
        account.setAccountNumber("1234567890");
        account.setStatus(Status.CLOSED);

        when(accountRepository.findByAccountNumber("1234567890"))
                .thenReturn(Optional.of(account));

        InvalidAccountOperationException exception =
                Assertions.assertThrows(
                        InvalidAccountOperationException.class,
                        () -> accountServiceImpl.closeAccount("1234567890")
                );

        Assertions.assertEquals(
                "Account has already been closed.",
                exception.getMessage()
        );

        verify(accountRepository).findByAccountNumber("1234567890");
        verify(accountRepository, never()).save(account);
    }


    @Test
    void shouldReturnAccountWhenAccountNumberExists() {

        Account account = new Account();
        account.setAccountNumber("1234567890");
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(java.util.Optional.of(account));
        Assertions.assertEquals(account, accountServiceImpl.getDetailsByAccountNumber("1234567890"));
        verify(accountRepository).findByAccountNumber("1234567890");
    }


    @Test
    void shouldThrowExceptionWhenAccountNumberDoesNotExist(){
        when(accountRepository.findByAccountNumber("1234567890"))
                .thenReturn(Optional.empty());
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> accountServiceImpl.getDetailsByAccountNumber("1234567890")
        );
        Assertions.assertEquals("Account not found", exception.getMessage());
    }

    @Test
    void shouldReturnEmptyListWhenCustomerHasNoAccounts(){

            when(accountRepository.findByCustomerId(999L)).thenReturn(List.of());

        List<Account> accounts = accountServiceImpl.getAccountDetailsByCustomerId(999L);

        Assertions.assertEquals(0, accounts.size());
        verify(accountRepository).findByCustomerId(999L);


    }


}
