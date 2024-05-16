package com.example.repick.dynamodb;

import com.example.repick.domain.user.entity.UserSmsVerificationInfo;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@EnableScan
public interface UserSmsVerificationInfoRepository extends CrudRepository<UserSmsVerificationInfo, String> {
    Optional<UserSmsVerificationInfo> findByUserIdAndPhoneNumberAndVerificationCode(Long userId, String phoneNumber, String verificationCode);
    void deleteAllByUserId(Long userId);
}
