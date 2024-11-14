package com.dev.repository;

import com.dev.models.Cart;
import com.dev.models.Restaurant;
import com.dev.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Query("SELECT r FROM Restaurant r JOIN FETCH r.owner o WHERE o.email = :email")
    Optional<Restaurant> findByOwnerEmail(String email);

    @Query("SELECT r FROM Restaurant r LEFT JOIN FETCH r.categoryFoods c JOIN r.owner o WHERE o.email = :email")
    Optional<Restaurant> findByOwnerEmailWithCategoryFood(String email);

    @Query("SELECT r FROM Restaurant r LEFT JOIN FETCH r.categoryIngredients c JOIN r.owner o WHERE o.email = :email")
    Optional<Restaurant> findByOwnerEmailWithCategoryIngredient(String email);

    @Query("SELECT r FROM Restaurant r LEFT JOIN FETCH r.foods c JOIN r.owner o WHERE o.email = :email ORDER BY c.createdAt DESC ")
    Optional<Restaurant> findByOwnerEmailWithFoods(String email);


    @Query("SELECT r FROM Restaurant r JOIN FETCH r.owner o where o.id = :id")
    Optional<Restaurant> findByOwnerId(Long id);

    @Query(value = "SELECT r FROM Restaurant r WHERE r.createdAt IS NOT NULL AND r.disable = false ",
           countQuery = "SELECT COUNT(*) FROM Restaurant r WHERE r.createdAt IS NOT NULL AND r.disable = false ")
    Page<Restaurant> fetchByRestaurantCreated(Pageable pageable);


    @Query("SELECT r from Restaurant r where (lower(r.name) like concat('%',lower(:s),'%') or lower(r.cuisineType) like concat('%',lower(:s),'%')) and r.disable = false ")
    List<Restaurant> findRestaurantBySearch(String s);


    @Query("SELECT r from Restaurant r JOIN FETCH r.categoryFoods c where r.id = :id order by c.name")
    Optional<Restaurant> findRestaurantWithCategory(Long id);

    @Query("SELECT r from Restaurant r JOIN FETCH r.owner o LEFT JOIN FETCH r.orders or left Join or.customer  where o.email = :email")
    Optional<Restaurant> findRestaurantByEmailWithOrders(String email);

    @Query("SELECT DISTINCT r FROM Restaurant r " +
            "WHERE (LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR EXISTS (SELECT 1 FROM r.foods f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "OR LOWER(r.cuisineType) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND r.disable = false ")
    List<Restaurant> searchRestaurantsByKeyword(@Param("keyword") String keyword);
}
