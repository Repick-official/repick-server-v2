package com.example.repick.domain.clothingSales.dto;

import com.example.repick.domain.clothingSales.entity.ClothingSalesType;
import com.example.repick.global.entity.Address;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class PostRequestDto implements Serializable {
    private ClothingSalesType clothingSalesType;
    private Address address;
    private Integer bagQuantity;
    private MultipartFile file;
}
