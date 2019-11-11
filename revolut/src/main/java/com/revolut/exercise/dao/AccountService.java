package com.revolut.exercise.dao;

import com.revolut.exercise.model.Account;
import com.revolut.exercise.model.MoneyServiceResponse;
import com.revolut.exercise.model.UserTransaction;

import java.util.Optional;


public interface AccountService {

    Optional<Account> getAccount(long accountId);
    Optional<Account> deleteAccount(long accountId);
    boolean addAccount(Account account);
    MoneyServiceResponse transferAccountBalance(UserTransaction userTransaction);
}
