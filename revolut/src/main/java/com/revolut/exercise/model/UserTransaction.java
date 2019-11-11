package com.revolut.exercise.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

public class UserTransaction {

	@JsonProperty
	private final long transactionId;
	@JsonProperty
	private final String currency;
	@JsonProperty
	private final BigDecimal amount;
	@JsonProperty
	private final long fromAccountId;
	@JsonProperty
	private final long toAccountId;

	public UserTransaction(long transactionId,
						   String currency,
						   BigDecimal amount,
						   long fromAccountId,
						   long toAccountId) {

		Objects.requireNonNull(currency, "Currency can not be null");
		Objects.requireNonNull(amount, "Amount can not be null");
		if (fromAccountId <= 0)
			throw new IllegalArgumentException("Sender AccountId needs to be greater than 0.");
		if (toAccountId <= 0)
			throw new IllegalArgumentException("Payee AccountId needs to be greater than 0.");
		if (transactionId <= 0)
			throw new IllegalArgumentException("TransactionId needs to be greater than 0.");

		this.transactionId = transactionId;
		this.currency = currency;
		this.amount = amount;
		this.fromAccountId = fromAccountId;
		this.toAccountId = toAccountId;
	}
	public long getTransactionId() {return transactionId;}

	public String getCurrency() {
		return currency;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public Long getFromAccountId() {
		return fromAccountId;
	}

	public Long getToAccountId() {return toAccountId; }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserTransaction that = (UserTransaction) o;
		return transactionId == that.transactionId &&
				fromAccountId == that.fromAccountId &&
				toAccountId == that.toAccountId &&
				Objects.equals(currency, that.currency) &&
				Objects.equals(amount, that.amount);
	}

	@Override
	public int hashCode() {
		return Objects.hash(transactionId, currency, amount, fromAccountId, toAccountId);
	}

	@Override
	public String toString() {
		return "UserTransaction{" +
				"transactionId=" + transactionId +
				", currency='" + currency + '\'' +
				", amount=" + amount +
				", fromAccountId=" + fromAccountId +
				", toAccountId=" + toAccountId +
				'}';
	}
}
