package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.EventEntity;
import org.example.entity.PlaceEntity;
import org.example.entity.request.EventRequest;
import org.example.entity.request.PlaceRequest;
import org.example.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final RestTemplate restTemplate;
    @Value("${spring.url.place}")
    private String placesURL;

    public void fetchAndSavePlaces() {
        PlaceEntity[] places = restTemplate.getForObject(placesURL, PlaceEntity[].class);

        if (places != null) {
            placeRepository.saveAll(Arrays.asList(places));
        }
    }

    public List<PlaceEntity> getAllPlaces() {
        return placeRepository.findAll();
    }

    public PlaceEntity getPlaceById(Long id) {
        return placeRepository.findById(id).orElse(null);
    }

    public PlaceEntity createPlace(PlaceRequest request) {
        PlaceEntity place = new PlaceEntity();
        place.setSlug(request.getSlug());
        place.setName(request.getName());
        return placeRepository.save(place);
    }

    public PlaceEntity updatePlace(Long id, PlaceRequest request) {
        PlaceEntity place = placeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Place with id " + id + " not found"));
        place.setSlug(request.getSlug());
        place.setName(request.getName());
        return placeRepository.save(place);
    }

    public void deletePlace(Long id) {
        PlaceEntity place = placeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Place with id " + id + " not found"));
        placeRepository.delete(place);
    }

}
