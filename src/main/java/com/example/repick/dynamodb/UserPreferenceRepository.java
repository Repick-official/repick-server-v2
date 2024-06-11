package com.example.repick.dynamodb;

import com.example.repick.domain.recommendation.entity.UserPreference;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface UserPreferenceRepository extends CrudRepository<UserPreference, Long> {
}
