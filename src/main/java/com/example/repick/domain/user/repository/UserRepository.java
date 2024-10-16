package com.example.repick.domain.user.repository;

import com.example.repick.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    Optional<User> findByProviderId(String providerId);

    List<User> findAllByDeletedAtBefore(LocalDateTime thirtyDaysAgo);

}
