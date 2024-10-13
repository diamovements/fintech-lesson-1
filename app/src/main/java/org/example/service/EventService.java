package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Event;
import org.example.entity.request.EventRequest;
import org.example.entity.response.EventResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    @Value("${spring.url.events}")
    private String eventsURL;

    private final RestTemplate restTemplate;

    private final CurrencyService currencyService;

    public List<Event> getPopularEvents(EventRequest request) {
        Date currentDate = new Date();
        Date actualSince;
        Date actualUntil;

        if (request.getDateFrom() != null && request.getDateTo() != null) {
            actualSince = request.getDateFrom();
            actualUntil = request.getDateTo();
        }
        else {
            actualSince = getStartOfWeek(currentDate);
            actualUntil = getEndOfWeek(currentDate);
        }
        log.info("Actual since: {}, actual until: {}", actualSince, actualUntil);
        String url = String.format("%s?actual_since=%s&actual_until=%s&fields=price,id,slug,title",
                eventsURL, actualSince.getTime() / 1000, actualUntil.getTime() / 1000);
        log.info("Url: {}", url);
        EventResponse eventResponse = restTemplate.getForObject(url, EventResponse.class);
        Double budgetRub = currencyService.convertToRub(request.getBudget(), request.getCurrency());
        log.info("Budget: {}", budgetRub);
        return eventResponse.getResults().stream()
                .filter(event -> {
                    Double eventPrice = parsePriceToDouble(event.getPrice());
                    return eventPrice != null && eventPrice <= budgetRub;
                })
                .collect(Collectors.toList());
    }

    private Double parsePriceToDouble(String price) {
        if (price == null || price.isEmpty() || price.equalsIgnoreCase("уточняется")) {
            return null;
        }
        String[] priceParts = price.replaceAll("[^0-9\\s]", "").trim().split("\\s+");
        if (priceParts.length > 0) {
            try {
                log.info("Price: {}", priceParts[0]);
                return Double.parseDouble(priceParts[0]);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
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
