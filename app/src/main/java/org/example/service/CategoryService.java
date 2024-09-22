package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dao.UniversalDatabase;
import org.example.entity.Category;
import org.example.exceptions.CategoryNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final UniversalDatabase<Integer, Category> db;

    public Category getCategory(int category_id) {
        Category category = db.get(category_id);
        if (category == null) {
            throw new CategoryNotFoundException("Category with id: " + category_id + " doesn't exist");
        }
        return category;
    }

    public Collection<Category> getAllCategories() {
        return db.getAll();
    }

    public void deleteCategory(int category_id) {
        Category category = getCategory(category_id);
        if (category == null) {
            throw new CategoryNotFoundException("Category with id: " + category_id + " doesn't exist");
        }
        db.remove(category_id);
    }

    public void addCategory(int category_id, Category category) {
        db.put(category_id, category);
    }

    public void updateCategory(int category_id, Category category) {
        db.update(category_id, category);
    }
}
