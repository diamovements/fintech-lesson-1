package org.example.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.timing.Timing;
import org.example.entity.Category;
import org.example.entity.Location;
import org.example.service.CategoryService;
import org.example.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@Timing
public class DataInitializer {
    private final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final CategoryService categoryService;
    private final LocationService locationService;

    @Value("${spring.url.category}")
    private String categoryURL;

    @Value("${spring.url.location}")
    private String locationURL;

    @Bean
    public CommandLineRunner doInit(RestTemplate restTemplate) {
        return args -> {
            logger.info("\u001B[34m" + "Starting initialization...");
            logger.info("Fetching categories...");
            Category[] categories = restTemplate.getForObject(categoryURL, Category[].class);
            if (categories != null) {
                logger.info("Fetched categories: {}", Arrays.stream(categories).collect(Collectors.toList()));
                for (Category c : categories) {
                    categoryService.addCategory(c.getId(), c);
                }
            }
            else {
                logger.error("Categories are null");
            }
            logger.info("Done fetching categories.");
            logger.info("Fetching places...");
            Location[] locations = restTemplate.getForObject(locationURL, Location[].class);
            if (locations != null) {
                logger.info("Fetched locations: {}", Arrays.stream(locations).collect(Collectors.toList()));
                for (Location l : locations) {
                    locationService.addLocation(l.getSlug(), l);
                }
            }
            else {
                logger.error("Locations are null");
            }
            logger.info("Done fetching locations.");
            logger.info("\u001B[34m" + "Finished initialization.");
        };
    }
}
