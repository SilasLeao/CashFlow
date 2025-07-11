package com.example.demo.repo;

import com.example.demo.models.transactions.Category;
import com.example.demo.models.enums.Nature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByNatureOrderByOrderIndexAsc(Nature nature);
    List<Category> findByActiveTrueOrderByOrderIndexAsc();
}
