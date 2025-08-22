package com.example.demo.service;

import com.example.demo.models.transactions.Category;
import com.example.demo.repo.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public boolean existsByName(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public Optional<Category> findById(UUID id) {
        return categoryRepository.findById(id);
    }

    public Category findByIdOrThrow(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
    }

    public Page<Category> findPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderIndex"));
        return categoryRepository.findAll(pageable);
    }

    public List<Category> findActiveCategories() {
        return categoryRepository.findByActiveTrueOrderByOrderIndexAsc();
    }
}
