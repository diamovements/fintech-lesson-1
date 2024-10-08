package org.example.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.config.SupportedCurrencies;
import org.example.exception.CurrencyNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@Slf4j
@RequiredArgsConstructor
@Setter
public class CurrencyService {

    private final RestTemplate restTemplate;
    private final CurrencyCacheService cacheService;
    private final SupportedCurrencies supportedCurrencies;

    @Getter
    @Value("${api.currency-url}")
    private String apiUrl;

    public Double convertCurrency(Double amount, String fromCurrency, String toCurrency) {
        if (amount <= 0 || fromCurrency == null || toCurrency == null) {
            log.error("Parameters are incorrect of missing");
            throw new IllegalArgumentException("Parameters are incorrect of missing");
        }
        log.info("Validating currency: {}", fromCurrency);
        supportedCurrencies.validateCurrency(fromCurrency);
        log.info("Validating currency: {}", toCurrency);
        supportedCurrencies.validateCurrency(toCurrency);
        Double fromRate = cacheService.getCurrencyRate(fromCurrency, restTemplate, apiUrl);
        Double toRate = cacheService.getCurrencyRate(toCurrency, restTemplate, apiUrl);
        if (toRate == 0.0) {
            throw new CurrencyNotFoundException("Currency not found");
        }
        return (fromRate / toRate) * amount;
    }

    public Double getCurrencyRate(String code) {
        log.info("Validating currency: {}", code);
        supportedCurrencies.validateCurrency(code);
        log.info("Fetching currency rate from cacheService for code: {}", code);
        return cacheService.getCurrencyRate(code, restTemplate, apiUrl);
    }
}
