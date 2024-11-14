package com.dev.repository;

import com.dev.models.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FoodRepository extends JpaRepository<Food,Long> {

    Optional<Food> findByName(String name);

    @Query("SELECT f FROM Food f LEFT JOIN f.ingredients i WHERE f.id = :id")
    Optional<Food> findByIdWithIngredients(long id);

    @Query("SELECT f FROM Food f LEFT JOIN f.restaurant i WHERE f.id = :id")
    Optional<Food> findByIdWithRestaurant(long id);

    @Query("SELECT f from Food f LEFT JOIN f.event e WHERE f IN :ids")
    List<Food> findByListFoodId(Set<Long> ids);
}
