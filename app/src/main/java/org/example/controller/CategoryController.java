package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.entity.Category;
import org.example.exceptions.CategoryNotFoundException;
import org.example.service.CategoryService;
import org.example.timing.Timing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/places/categories")
@Timing
public class CategoryController {

    private final CategoryService categoryService;
    private final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable("id") int category_id) {
        try {
            logger.info("Deleting category: {}", category_id);
            categoryService.deleteCategory(category_id);
            return ResponseEntity.ok("Category has been deleted successfully");
        } catch (CategoryNotFoundException ex) {
            logger.warn("Category doesn't exist: {}", category_id);
            return ResponseEntity.badRequest().body("Category cannot be deleted, because it doesn't exist");
        }
    }

    @PostMapping()
    public ResponseEntity<String> addCategory(@RequestBody Category category) {
        logger.info("Adding category: {}", category.toString());
        categoryService.addCategory(category.getId(), category);
        return ResponseEntity.ok("Category has been added successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable("id") int category_id, @RequestBody Category category) {
        logger.info("Updating category: {}", category.toString());
        categoryService.updateCategory(category_id, category);
        return ResponseEntity.ok("Category has been updated successfully");
    }

    @GetMapping()
    public ResponseEntity<Collection<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategory(@PathVariable("id") int category_id) {
        try {
            return ResponseEntity.ok(categoryService.getCategory(category_id));
        } catch (CategoryNotFoundException ex) {
            logger.warn("Category doesn't exist: {}", category_id);
            return ResponseEntity.badRequest().body("No category with this id");
        }
    }
}
