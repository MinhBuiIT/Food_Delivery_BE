package com.dev.repository;

import com.dev.models.CategoryIngredient;
import com.dev.models.IngredientItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface IngredientItemRepository extends JpaRepository<IngredientItem,Long> {

    @Query("SELECT i FROM IngredientItem i JOIN FETCH i.categoryIngredient WHERE i.id = :id")
    Optional<IngredientItem> findByIdWithCategoryIngredient(Long id);

    List<IngredientItem> findByNameIn(Set<String> names);

    List<IngredientItem> findByCategoryIngredient(CategoryIngredient categoryIngredient);

    @Query("SELECT i FROM IngredientItem i JOIN FETCH i.categoryIngredient c WHERE i.name = :name AND c.id = :categoryId")
    Optional<IngredientItem> findByNameAndCategoryIngredient(@Param("name") String name, @Param("categoryId") Long categoryId);

    @Query("SELECT i FROM IngredientItem i JOIN FETCH i.categoryIngredient where i.id in :ids")
    List<IngredientItem> fetchAllByIngredientId(List<Long> ids);
}
