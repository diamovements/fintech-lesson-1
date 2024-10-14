package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Event;
import org.example.entity.request.EventRequest;
import org.example.entity.response.EventResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    @Value("${spring.url.event}")
    private String eventURL;

    private final RestTemplate restTemplate;

    private final CurrencyService currencyService;

    @Async
    public CompletableFuture<List<Event>> getPopularEvents(EventRequest request) {
        Date currentDate = new Date();
        Date actualSince = request.getDateFrom();
        Date actualUntil = request.getDateTo();

        if (actualSince == null || actualUntil == null) {
            actualSince = getStartOfWeek(currentDate);
            actualUntil = getEndOfWeek(currentDate);
        }

        log.info("Actual since: {}, actual until: {}", actualSince, actualUntil);
        String url = String.format("%s&actual_since=%s&actual_until=%s",
                eventURL, actualSince.getTime() / 1000, actualUntil.getTime() / 1000);
        log.info("Url: {}", url);
        CompletableFuture<EventResponse> eventFuture = CompletableFuture.supplyAsync(() -> restTemplate.getForObject(url, EventResponse.class));
        CompletableFuture<BigDecimal> budgetRubFuture = currencyService.convertToRub(request.getBudget(), request.getCurrency());
        CompletableFuture.allOf(eventFuture, budgetRubFuture).join();

        CopyOnWriteArrayList<Event> resultList = new CopyOnWriteArrayList<>();
        CompletableFuture<Void> combinedFutures = eventFuture.thenAcceptBoth(budgetRubFuture,
                (eventResponse, budget) -> {
            eventResponse.getResults().stream().filter(event -> {
                BigDecimal eventPrice = parsePrice(event.getPrice());
                return eventPrice != null && eventPrice.compareTo(budget) <= 0;
            }).forEach(resultList::add);
        });
        return combinedFutures.thenApplyAsync(res -> resultList);
    }

    private BigDecimal parsePrice(String price) {
        if (price == null || price.isEmpty() || price.equalsIgnoreCase("уточняется")) {
            return null;
        }
        String[] priceParts = price.replaceAll("[^0-9\\s]", "").trim().split("\\s+");
        if (priceParts.length > 0) {
            try {
                log.info("Price: {}", priceParts[0]);
                return new BigDecimal(priceParts[0]);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public Date parseDate(String date) {
        if (date == null) {
            return null;
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            return formatter.parse(date);
        } catch (ParseException e) {
            log.error("Date parse error", e);
            throw new RuntimeException(e);
        }
    }

    private Date getStartOfWeek(Date now) {
        LocalDate localDate = now.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate startOfWeek = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return Date.from(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Date getEndOfWeek(Date now) {
        LocalDate localDate = now.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate startOfWeek = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        return Date.from(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
