package org.example.config;

import lombok.extern.slf4j.Slf4j;
import org.example.exception.CurrencyNotFoundException;
import org.springframework.stereotype.Component;
import java.util.Currency;
import java.util.Set;
@Component
@Slf4j
public class SupportedCurrencies {
    private static final Set<Currency> currencies = Currency.getAvailableCurrencies();

    public void validateCurrency(String code) {
        try {
            Currency currency = Currency.getInstance(code);
            if (!currencies.contains(currency)) {
                log.error("Currency not found: {}", code);
                throw new CurrencyNotFoundException("Currency not found");
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid currency code: {}", code);
            throw new IllegalArgumentException("Invalid currency code");
        }
    }
}
