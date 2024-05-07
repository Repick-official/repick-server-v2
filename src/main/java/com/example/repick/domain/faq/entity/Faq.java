package com.example.repick.domain.faq.entity;

import com.example.repick.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) @Getter
public class Faq extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    private String answer;

    @Builder
    public Faq(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public void updateFaq(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

}
