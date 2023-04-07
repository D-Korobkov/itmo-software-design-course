package org.example.bank;

import org.example.model.Currency;

public class MockCurrencyRateService implements CurrencyRateService {
    @Override
    public double getRate(Currency currency, Currency settlementCurrency) {
        return getPriceInRub(currency) / getPriceInRub(settlementCurrency);
    }

    private double getPriceInRub(Currency currency) {
        return switch (currency) {
            case RUB -> 1;
            case USD -> 80;
            case EUR -> 90;
        };
    }
}
