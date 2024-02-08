package com.example.repick.dynamodb;

import com.example.repick.domain.fcmtoken.entity.UserFcmTokenInfo;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface UserFcmTokenInfoRepository extends CrudRepository<UserFcmTokenInfo, Long> {
}
