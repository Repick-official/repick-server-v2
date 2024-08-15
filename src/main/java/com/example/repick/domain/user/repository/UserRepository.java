package com.example.repick.domain.user.repository;

import com.example.repick.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByProviderId(String providerId);

    Long countIsDeletedFalse();
    Long countIsDeletedFalseByCreatedDateAfter(LocalDateTime createdDate);

}
