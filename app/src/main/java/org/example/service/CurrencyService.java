package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.response.CurrencyResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {
    private final WebClient webClient;

    @Value("${spring.url.currency}")
    private String currencyUrl;

    public Mono<BigDecimal> convertToRub(BigDecimal budget, String currency) {
        String newUrl = currencyUrl + "/" + currency;
        log.info("Url: {}", newUrl);
        return webClient.get()
                .uri(newUrl)
                .retrieve()
                .bodyToMono(CurrencyResponse.class)
                .flatMap(response -> {
                    if (response != null && response.getRates().containsKey("RUB")) {
                        BigDecimal rubRate = response.getRates().get("RUB");
                        log.info("Rub rate: {}", rubRate);
                        return Mono.just(budget.multiply(rubRate));
                    } else {
                        log.error("Currency not found");
                        return Mono.error(new IllegalArgumentException("Currency not found"));
                    }
                })
                .doOnError(e -> log.error("Error converting currency: {}", e.getMessage()));
    }
}
