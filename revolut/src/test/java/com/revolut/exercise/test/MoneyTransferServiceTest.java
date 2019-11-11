package com.revolut.exercise.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.exercise.dao.AccountService;
import com.revolut.exercise.model.MoneyServiceResponse;
import com.revolut.exercise.model.UserTransaction;
import com.revolut.exercise.service.MoneyTransferService;

import com.sun.net.httpserver.HttpExchange;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.concurrent.*;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doAnswer;

public class MoneyTransferServiceTest {

    @BeforeClass
    public void setUp(){


    }
    @Test
    public void RequestMethodWithPost() throws Exception{
        AccountService accountService = mock(AccountService.class);
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        Executor executorService =  new CurrentThreadExecutor();
        HttpExchange exchange = mock(HttpExchange.class);
        InputStream inputStream = mock(InputStream.class);
        OutputStream outputStream = mock(OutputStream.class);
        UserTransaction transaction = new UserTransaction(1, "GBP", BigDecimal.valueOf(1000), 1234L, 5678L);

        MoneyTransferService moneyTransferService = new MoneyTransferService(accountService,objectMapper,executorService);

        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(inputStream);
        when(exchange.getResponseBody()).thenReturn(outputStream);
        when(objectMapper.readValue(any(InputStream.class), eq(UserTransaction.class))).thenReturn(transaction);

        moneyTransferService.handle(exchange);

        verify(objectMapper, times(1)).readValue(any(InputStream.class), eq(UserTransaction.class));
        verify(accountService, times(1)).transferAccountBalance(transaction);
        verify(objectMapper, times(1)).writeValueAsBytes(any());
    }
    @Test
    public void ReturnErrorIfRequestMethodNotPost() throws Exception{
        AccountService accountService = mock(AccountService.class);
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        Executor executorService =  new CurrentThreadExecutor();
        HttpExchange exchange = mock(HttpExchange.class);
        InputStream inputStream = mock(InputStream.class);
        OutputStream outputStream = mock(OutputStream.class);
        UserTransaction transaction = new UserTransaction(1, "GBP", BigDecimal.valueOf(1000), 1234L, 5678L);

        MoneyTransferService moneyTransferService = new MoneyTransferService(accountService,objectMapper,executorService);

        when(exchange.getRequestMethod()).thenReturn("PUT");
        when(exchange.getRequestBody()).thenReturn(inputStream);
        when(exchange.getResponseBody()).thenReturn(outputStream);
        when(objectMapper.readValue(any(InputStream.class), eq(UserTransaction.class))).thenReturn(transaction);

        moneyTransferService.handle(exchange);

        verify(objectMapper, times(1)).readValue(any(InputStream.class), eq(UserTransaction.class));
        verify(accountService, times(0)).transferAccountBalance(transaction);
        verify(objectMapper, times(1)).writeValueAsBytes(any());
    }
    public class CurrentThreadExecutor implements Executor {
        public void execute(Runnable r) {
            r.run();
        }
    }

}
