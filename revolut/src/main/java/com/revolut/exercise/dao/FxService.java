package com.revolut.exercise.dao;

import java.math.BigDecimal;

public interface FxService {

    static BigDecimal getFxRate(String fromCurrency, String toCurrency){
        return BigDecimal.valueOf(1);
    }
}
