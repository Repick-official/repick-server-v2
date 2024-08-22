package com.example.repick.domain.user.repository;

import com.example.repick.domain.user.dto.GetUserStatistics;
import com.example.repick.domain.user.entity.Gender;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import static com.example.repick.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public GetUserStatistics getUserStatistics() {
        Tuple result = queryFactory
                .select(
                        user.count(), // 총 유저 수
                        new CaseBuilder()
                                .when(user.createdDate.after(LocalDateTime.now().minusMonths(1))).then(1).otherwise(0).sum(), // 신규 유저 수
                        new CaseBuilder()
                                .when(user.gender.eq(Gender.FEMALE)).then(1).otherwise(0).sum(), // 여성 유저 수
                        new CaseBuilder()
                                .when(user.gender.eq(Gender.MALE)).then(1).otherwise(0).sum() // 남성 유저 수
                )
                .from(user)
                .where(user.isDeleted.isFalse())
                .fetchOne();

        long totalUserCount = result.get(0, Number.class).longValue();
        long newUserCount = result.get(1, Number.class).longValue();
        long femaleUserCount = result.get(2, Number.class).longValue();
        long maleUserCount = result.get(3, Number.class).longValue();

        double femaleRatio = (double) femaleUserCount / totalUserCount;
        double maleRatio = (double) maleUserCount / totalUserCount;

        return GetUserStatistics.of(totalUserCount, newUserCount, femaleRatio, maleRatio);
    }

}