package org.example.bank;

import org.example.model.Currency;

public interface CurrencyRateService {
    double getRate(Currency currency, Currency settlementCurrency);
}
