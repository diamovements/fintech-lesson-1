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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {

    private final EventService eventService;

    @GetMapping()
    public ResponseEntity<List<Event>> getEvents(@RequestParam(name = "dateFrom", required = false) String dateFrom,
                                                 @RequestParam(name = "dateTo", required = false) String dateTo,
                                                 @RequestParam(name = "budget") Double budget,
                                                 @RequestParam(name = "currency") String currency) throws ParseException {
        EventRequest request = new EventRequest();
        log.info("Date from: {}, date to: {}", dateFrom, dateTo);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date from = format.parse(dateFrom);
        Date to = format.parse(dateTo);
        request.setDateFrom(from);
        request.setDateTo(to);
        request.setCurrency(currency);
        request.setBudget(budget);
        log.info("Request: " + request);

        List<Event> events = eventService.getPopularEvents(request);

        return ResponseEntity.ok(events);
    }

}
