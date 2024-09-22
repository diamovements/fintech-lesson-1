package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dao.UniversalDatabase;
import org.example.entity.Location;
import org.example.exceptions.LocationNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final UniversalDatabase<String, Location> db;

    public Location getLocation(String slug) {
        Location location = db.get(slug);
        if (location == null) {
            throw new LocationNotFoundException("Location with slug: " + slug + " doesn't exist");
        }
        return location;
    }

    public Collection<Location> getAllLocations() {
        return db.getAll();
    }

    public void addLocation(String slug, Location location) {
        db.put(slug, location);
    }

    public void deleteLocation(String slug) {
        Location location = getLocation(slug);
        if (location == null) {
            throw new LocationNotFoundException("Location with slug: " + slug + " doesn't exist");
        }
        db.remove(slug);
    }

    public void updateLocation(String slug, Location location) {
        db.update(slug, location);
    }
}
