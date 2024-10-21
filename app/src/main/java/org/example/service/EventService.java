package org.example.service;

import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.example.entity.EventEntity;
import org.example.entity.EventFilter;
import org.example.entity.PlaceEntity;
import org.example.entity.request.EventRequest;
import org.example.repository.EventRepository;
import org.example.repository.PlaceRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final PlaceRepository placeRepository;
    public List<EventEntity> getAllEvents() {
        return eventRepository.findAll();
    }

    public EventEntity getEventById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    public EventEntity createEvent(EventRequest request) {
        PlaceEntity place = placeRepository.findById(request.getPlaceId()).orElseThrow(() ->
                new IllegalArgumentException("Place not found"));
        EventEntity event = new EventEntity();
        event.setTitle(request.getName());
        event.setFromDate(request.getDateFrom());
        event.setPlace_id(place);
        event.setToDate(request.getDateTo());

        return eventRepository.save(event);
    }

    public EventEntity updateEvent(Long id, EventRequest request) {
        EventEntity existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event with id " + id + " not found"));

        if (request.getPlaceId() != null) {
            PlaceEntity place = placeRepository.findById(request.getPlaceId())
                    .orElseThrow(() -> new IllegalArgumentException("Place with id " + request.getPlaceId() + " not found"));
            existingEvent.setPlace_id(place);
        }

        existingEvent.setTitle(request.getName());
        existingEvent.setFromDate(request.getDateFrom());
        existingEvent.setToDate(request.getDateTo());

        return eventRepository.save(existingEvent);
    }

    public void deleteEvent(Long id) {
        EventEntity event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event with id " + id + " not found"));
        eventRepository.delete(event);
    }

    public Optional<List<EventEntity>> findByFilter(EventFilter filter) {
        Specification<EventEntity> spec = Specification.where(null);

        if (filter.getName() != null && !filter.getName().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + filter.getName().toLowerCase() + "%")
            );
        }
        if (filter.getPlace_id() != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                root.fetch("place", JoinType.LEFT);
                return criteriaBuilder.equal(root.get("place").get("id"), filter.getPlace_id());
            });
        }
        if (filter.getFromDate() != null && filter.getToDate() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.between(root.get("fromDate"), filter.getFromDate(), filter.getToDate())
            );
        } else if (filter.getFromDate() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("fromDate"), filter.getFromDate())
            );
        } else if (filter.getToDate() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("toDate"), filter.getToDate())
            );
        }
        return eventRepository.findAll(spec);
    }
}
