package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.config.timing.Timing;
import org.example.entity.Location;
import org.example.exceptions.LocationNotFoundException;
import org.example.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Timing
public class LocationController {

    private final LocationService locationService;
    private final Logger logger = LoggerFactory.getLogger(LocationController.class);

    @DeleteMapping("/{slug}")
    public ResponseEntity<?> deleteLocation(@PathVariable("slug") String slug) {
        try {
            logger.info("Deleting location: {}", slug);
            locationService.deleteLocation(slug);
            return ResponseEntity.ok("Location has been deleted successfully");
        } catch (LocationNotFoundException ex) {
            logger.warn("Location doesn't exist: {}", slug);
            return ResponseEntity.badRequest().body("Location cannot be deleted, because it doesn't exist");
        }
    }

    @PostMapping()
    public ResponseEntity<String> addLocation(@RequestBody Location location) {
        logger.info("Adding location: {}", location.toString());
        locationService.addLocation(location.getSlug(), location);
        return ResponseEntity.ok("Location has been added successfully");
    }

    @PutMapping("/{slug}")
    public ResponseEntity<String> updateLocation(@PathVariable("slug") String slug, @RequestBody Location location) {
        logger.info("Updating location: {}", location.toString());
        locationService.updateLocation(slug, location);
        return ResponseEntity.ok("Location has been updated successfully");
    }

    @GetMapping()
    public ResponseEntity<Collection<Location>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<?> getLocation(@PathVariable("slug") String slug) {
        try {
            return ResponseEntity.ok(locationService.getLocation(slug));
        } catch (LocationNotFoundException ex) {
            logger.warn("Location doesn't exist: {}", slug);
            return ResponseEntity.badRequest().body("No location with this id");
        }
    }
}
