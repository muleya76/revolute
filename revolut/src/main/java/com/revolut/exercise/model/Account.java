package com.revolut.exercise.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {

    private final long accountId;
    private final String userName;
    private BigDecimal balance;
    private final String currency;

     public Account(long accountId, String userName, BigDecimal balance, String currencyCode) {
        this.accountId = accountId;
        this.userName = userName;
        this.balance = balance;
        this.currency = currencyCode;
    }

    public long getAccountId() {
        return accountId;
    }

    public String getUserName() {
        return userName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal newBalance) {
            balance = newBalance;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountId == account.accountId &&
                Objects.equals(userName, account.userName) &&
                Objects.equals(balance, account.balance) &&
                Objects.equals(currency, account.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, userName, balance, currency);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", userName='" + userName + '\'' +
                ", balance=" + balance +
                ", currencyCode='" + currency + '\'' +
                '}';
    }
}
