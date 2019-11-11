package com.revolut.exercise.model;


import java.util.Optional;

public class MoneyServiceResponse {

    private final boolean isError;
    private final Optional<String> message;

    public MoneyServiceResponse(boolean isError, Optional<String> message) {
        this.isError = isError;
        this.message = message;
    }

    public boolean isError() {
        return isError;
    }

    public Optional<String> getMessage() {
        return message;
    }
}



