package com.example.repick.domain.clothingSales.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BoxState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "box_state_id")
    private Long id;    // BoxState의 id

//    @Enumerated(EnumType.STRING)
//    @Column(name = "sales_status")
//    private SalesStatus salesStatus;

    @ManyToOne
    @JoinColumn(name = "box_collect_id")
    private BoxCollect boxCollect;
}






