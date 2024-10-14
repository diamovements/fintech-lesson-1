package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.response.CurrencyResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {
    private final RestTemplate restTemplate;

    @Value("${spring.url.currency}")
    private String currencyUrl;

    @Async
    public CompletableFuture<BigDecimal> convertToRub(BigDecimal budget, String currency) {
        String newUrl = currencyUrl + "/" + currency;
        log.info("Url: {}", newUrl);
        CurrencyResponse response = restTemplate.getForObject(newUrl, CurrencyResponse.class);
        log.info("Response: {}", response);
        if (response != null && response.getRates().containsKey("RUB")) {
            BigDecimal rubRate = response.getRates().get("RUB");
            log.info("Rate of RUB: {}", rubRate);
            log.info("MBudget in rub: {}", rubRate.multiply(budget));
            return CompletableFuture.completedFuture(rubRate.multiply(budget));
        }
        else {
            log.error("Currency not found");
            throw new IllegalArgumentException("Currency not found");
        }
    }
}
