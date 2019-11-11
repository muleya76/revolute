package com.revolut.exercise.dao;

public abstract class DataStoreFactory {

	private static int IN_MEMORY_DATA_STORE = 1;

	public static DataStoreInterface getAccountDataStore(int code) {

		switch (code) {
		default:
			return new InMemoryDataStore();
		}
	}
}
