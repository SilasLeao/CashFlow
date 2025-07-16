package com.example.demo.service;

import com.example.demo.models.transactions.Category;
import com.example.demo.repo.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public boolean existsByName(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public Category findByIdOrThrow(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
    }

    public List<Category> findActiveCategories() {
        return categoryRepository.findByActiveTrueOrderByOrderIndexAsc();
    }
}
