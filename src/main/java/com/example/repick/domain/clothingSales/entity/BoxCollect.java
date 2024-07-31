package com.example.repick.domain.clothingSales.entity;

import com.example.repick.domain.user.entity.User;
import com.example.repick.global.entity.Address;
import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class BoxCollect extends ClothingSales {
}
