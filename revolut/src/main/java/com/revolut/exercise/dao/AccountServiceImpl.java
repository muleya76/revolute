package com.revolut.exercise.dao;

import com.revolut.exercise.model.Account;
import com.revolut.exercise.model.MoneyServiceResponse;
import com.revolut.exercise.model.UserTransaction;
import org.apache.log4j.Logger;

//import org.apache.;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static com.revolut.exercise.dao.FxService.*;

public class AccountServiceImpl implements AccountService {

	private static final Logger log = Logger.getLogger(AccountServiceImpl.class);

	private static final long LOCK_WAIT_MS = 500;

	private final DataStoreInterface dataStore;
	private final FxService fxService;
	private final ConcurrentHashMap<Long, ReentrantLock> lockMap;

	public AccountServiceImpl(DataStoreInterface impl, FxService fxService) {
		this.dataStore = impl;
		this.fxService = fxService;
		this.lockMap = new ConcurrentHashMap<>();
	}

	@Override
	public Optional<Account> getAccount(long accountId) {
		return dataStore.getAccount(accountId);
	}

	@Override
	public Optional<Account> deleteAccount(long accountId) {

	    return dataStore.deleteAccount(accountId);
	}

	@Override
	public boolean addAccount(Account account) {
		Objects.requireNonNull(account, "Account can not be null.");
		return dataStore.addAccount(account) != null;
	}

	@Override
	public MoneyServiceResponse transferAccountBalance(UserTransaction userTransaction) {

		log.info("Received request : " + userTransaction);

		long fromAccountId = userTransaction.getFromAccountId();
		long toAccountId = userTransaction.getToAccountId();
		BigDecimal amount = userTransaction.getAmount();
		String currency = userTransaction.getCurrency();

		ReentrantLock firstLock = (fromAccountId < toAccountId)
					? getLockForAccount(fromAccountId) : getLockForAccount(toAccountId);

		try{
			if(firstLock.tryLock(LOCK_WAIT_MS, TimeUnit.MILLISECONDS)){
				ReentrantLock secondLock = (fromAccountId < toAccountId)
						? getLockForAccount(toAccountId) : getLockForAccount(fromAccountId);
				try{
					if(secondLock.tryLock(LOCK_WAIT_MS, TimeUnit.MILLISECONDS)){
						Optional<Account> optFromAccount = dataStore.getAccount(fromAccountId);
						Optional<Account> optToAccount = dataStore.getAccount(toAccountId);

						if(!optFromAccount.isPresent() || !optToAccount.isPresent()){
							return new MoneyServiceResponse(true, Optional.of("Account details not correct"));
						}

						Account fromAccount = optFromAccount.get();
						Account toAccount = optToAccount.get();

						fromAccount.setBalance(fromAccount.getBalance().subtract(amount.multiply(getFxRate(currency, fromAccount.getCurrency()))));
						toAccount.setBalance(toAccount.getBalance().add(amount.multiply(getFxRate(currency, toAccount.getCurrency()))));

						return new MoneyServiceResponse(false, Optional.empty());
					}
				}
				catch (Exception ex1){
					log.error("Thread interrupted while waiting for lock on Account "+ ((fromAccountId < toAccountId) ? toAccountId : fromAccountId ));
					Thread.currentThread().interrupt();
					return new MoneyServiceResponse(true, Optional.of("Interrupted while waiting for lock on Destination AccountId :  "
							+ ((fromAccountId < toAccountId) ? toAccountId : fromAccountId)));
				}
				finally {
					if(secondLock != null) secondLock.unlock();
				}
			}
		}
		catch (InterruptedException ex){
			log.error("Thread interrupted while waiting for lock on Account "+ ((fromAccountId < toAccountId) ? fromAccountId : toAccountId ));
			Thread.currentThread().interrupt();
			return new MoneyServiceResponse(true, Optional.of("Interrupted while waiting for lock on Source AccountId : "
					+ ((fromAccountId < toAccountId) ? fromAccountId : toAccountId)));
		}
		finally {
			if(firstLock != null) firstLock.unlock();
		}
		return new MoneyServiceResponse(true, Optional.of("Error in processing"));



	}

	private ReentrantLock getLockForAccount(long accountId){

		ReentrantLock lock = new ReentrantLock();
		ReentrantLock oldLock = lockMap.putIfAbsent(accountId, lock);
		if(oldLock != null) return oldLock;
		return  lock;
	}
}
