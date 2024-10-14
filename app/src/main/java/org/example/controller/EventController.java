package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Event;
import org.example.entity.request.EventRequest;
import org.example.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {

    private final EventService eventService;

    @GetMapping()
    public CompletableFuture<ResponseEntity<List<Event>>> getEvents(@RequestParam(name = "dateFrom", required = false) String dateFrom,
                                                                   @RequestParam(name = "dateTo", required = false) String dateTo,
                                                                   @RequestParam(name = "budget") BigDecimal budget,
                                                                   @RequestParam(name = "currency") String currency) {
        EventRequest request = new EventRequest();
        log.info("Date from: {}, date to: {}", dateFrom, dateTo);
        request.setDateFrom(eventService.parseDate(dateFrom));
        request.setDateTo(eventService.parseDate(dateTo));
        request.setCurrency(currency);
        request.setBudget(budget);
        log.info("Request: " + request);
        return eventService.getPopularEvents(request).thenApply(ResponseEntity::ok);
    }

}
