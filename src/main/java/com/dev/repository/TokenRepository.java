package com.dev.repository;

import com.dev.models.Token;
import com.dev.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUser(User user);
    void deleteByUser(User user);

    @Transactional
    void deleteTokenByUser(User user);

    Optional<Token> findTokenByRefreshToken(String token);
}
