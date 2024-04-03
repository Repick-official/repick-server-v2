package com.example.repick.domain.clothingSales.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RepickBagState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "repick_bag_state_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "sales_status")
    private SalesStatus salesStatus;

    @ManyToOne
    @JoinColumn(name = "repick_bag_apply_id")
    private RepickBagApply repickBagApply;

}






