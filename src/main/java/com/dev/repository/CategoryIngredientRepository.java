package com.dev.repository;

import com.dev.models.CategoryFood;
import com.dev.models.CategoryIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryIngredientRepository extends JpaRepository<CategoryIngredient,Long> {
    Optional<CategoryIngredient> findByName(String name);

    @Query("select c from CategoryIngredient c left join fetch c.restaurant r where c.name = :name and r.id = :restaurantId")
    Optional<CategoryIngredient> findByNameAndRestaurantId(String name, Long restaurantId);

    @Query("select c from CategoryIngredient c left JOIN fetch c.ingredients where c.name = :name")
    Optional<CategoryIngredient> findByNameWithIngredientsItem(String name);

    @Query("select c from CategoryIngredient c JOIN FETCH c.restaurant r JOIN FETCH r.owner o where o.email = :email")
    List<CategoryIngredient> findByRestaurantEmail(String email);

}
