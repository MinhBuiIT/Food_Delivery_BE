package com.dev.repository;

import com.dev.models.CategoryFood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryFoodRepository extends JpaRepository<CategoryFood,Long> {
    Optional<CategoryFood> findByName(String name);
}
