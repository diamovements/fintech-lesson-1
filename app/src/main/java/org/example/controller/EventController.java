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
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {

    private final EventService eventService;

    @GetMapping()
    public Mono<ResponseEntity<List<Event>>> getEvents(@RequestParam(name = "dateFrom", required = false) String dateFrom,
                                                       @RequestParam(name = "dateTo", required = false) String dateTo,
                                                       @RequestParam(name = "budget") BigDecimal budget,
                                                       @RequestParam(name = "currency") String currency) {
        EventRequest request = new EventRequest();
        log.info("Date from: {}, date to: {}", dateFrom, dateTo);
        request.setDateFrom(eventService.parseDate(dateFrom));
        request.setDateTo(eventService.parseDate(dateTo));
        request.setCurrency(currency);
        request.setBudget(budget);
        log.info("Request: " + request.getCurrency());
        return eventService.getPopularEvents(request)
                .map(ResponseEntity::ok);
    }

}
