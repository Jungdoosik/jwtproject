package com.doosik.jwtproject.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.doosik.jwtproject.domain.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUsername(String username);
    void deleteByUsername(String username);
}
