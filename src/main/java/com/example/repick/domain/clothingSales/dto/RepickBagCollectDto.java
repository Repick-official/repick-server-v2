package com.example.repick.domain.clothingSales.dto;

import com.example.repick.global.entity.Address;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class RepickBagCollectDto implements Serializable {
    private Address address;
    private Integer bagQuantity;
    private String collectionDate;
    private MultipartFile file;
    private Long repickBagApplyId;
}
