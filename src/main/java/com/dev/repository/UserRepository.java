package com.dev.repository;

import com.dev.models.Restaurant;
import com.dev.models.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.favorites f where f.id = :restaurantId")
    List<User> fetchUsersByFavorites(Long restaurantId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.addresses where u.email = :email")
    Optional<User> findByEmailWithAddress(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.orders o where u.email = :email ")
    Optional<User> findByEmailWithOrder(String email);
}
