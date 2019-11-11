package com.revolut.exercise.dao;

import com.revolut.exercise.model.Account;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDataStore implements DataStoreInterface {

    private final ConcurrentHashMap<Long, Account> accountMap;

    public InMemoryDataStore() {
        this.accountMap = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<Account> getAccount(long accountId) {
        return Optional.ofNullable(accountMap.get(accountId));
    }

    @Override
    public Optional<Account> deleteAccount(long accountId) {
        return  Optional.ofNullable(accountMap.remove(accountId));
    }

    @Override
    public Account addAccount(Account account) {
        return accountMap.putIfAbsent(account.getAccountId(), account);
    }
}
