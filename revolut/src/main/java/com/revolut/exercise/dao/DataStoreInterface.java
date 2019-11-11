package com.revolut.exercise.dao;

import com.revolut.exercise.model.Account;
import com.revolut.exercise.model.MoneyServiceResponse;
import com.revolut.exercise.model.UserTransaction;

import java.util.Optional;

public interface DataStoreInterface {
    Optional<Account> getAccount(long accountId);
    Optional<Account> deleteAccount(long accountId);
    Account addAccount(Account account);
}
