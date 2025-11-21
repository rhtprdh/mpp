package com.soms.auth_service.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.soms.auth_service.model.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}
