package com.revolut.exercise.test;

import com.revolut.exercise.dao.AccountService;
import com.revolut.exercise.dao.AccountServiceImpl;
import com.revolut.exercise.dao.DataStoreInterface;
import com.revolut.exercise.dao.FxService;
import com.revolut.exercise.model.Account;
import com.revolut.exercise.model.MoneyServiceResponse;
import com.revolut.exercise.model.UserTransaction;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class AccountServiceImplTest {

    @BeforeClass
    public void setUp(){


    }

    @Test
    public void transferBalanceTest() {

        FxService fxService = mock(FxService.class);
        DataStoreInterface dataStore = mock(DataStoreInterface.class);

        Account account1= new Account(8888,"Account1",BigDecimal.valueOf(10000),"GBP");
        Account account2 = new Account(9999,"Account2",BigDecimal.valueOf(500),"GBP");

        UserTransaction transaction = new UserTransaction(1, "GBP", BigDecimal.valueOf(1000), 8888, 9999);

        when(dataStore.getAccount(8888)).thenReturn(Optional.of(account1));
        when(dataStore.getAccount(9999)).thenReturn(Optional.of(account2));

        AccountService accountService = new AccountServiceImpl(dataStore, fxService);
        MoneyServiceResponse response = accountService.transferAccountBalance(transaction);

        assertNotNull(response);
        assertFalse(response.isError());
        assertFalse(response.getMessage().isPresent());
        assertEquals(account1.getBalance().longValue(),9000);
        assertEquals(account2.getBalance().longValue(),1500);
        verify(dataStore, times(1)).getAccount(8888);
        verify(dataStore, times(1)).getAccount(9999);

    }

    @Test
    public void transferBalanceInvalidAccountTest() {

        FxService fxService = mock(FxService.class);
        DataStoreInterface dataStore = mock(DataStoreInterface.class);

        Account account1= new Account(8888,"Account1",BigDecimal.valueOf(10000),"GBP");

        UserTransaction transaction = new UserTransaction(1, "GBP", BigDecimal.valueOf(1000), 8888, 5555);

        when(dataStore.getAccount(8888)).thenReturn(Optional.of(account1));
        when(dataStore.getAccount(5555)).thenReturn(Optional.ofNullable(null));

        AccountService accountService = new AccountServiceImpl(dataStore, fxService);
        MoneyServiceResponse response = accountService.transferAccountBalance(transaction);

        assertNotNull(response);
        assertTrue(response.isError());
        assertTrue(response.getMessage().isPresent());
        assertEquals(account1.getBalance().longValue(),10000);
        verify(dataStore, times(1)).getAccount(8888);
        verify(dataStore, times(1)).getAccount(5555);
    }
}
