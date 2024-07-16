package com.example.repick.domain.clothingSales.service;

import com.example.repick.domain.clothingSales.dto.*;
import com.example.repick.domain.clothingSales.entity.BoxCollect;
import com.example.repick.domain.clothingSales.entity.BoxCollectState;
import com.example.repick.domain.clothingSales.entity.BoxCollectStateType;
import com.example.repick.domain.clothingSales.repository.BoxCollectRepository;
import com.example.repick.domain.clothingSales.repository.BoxCollectStateRepository;
import com.example.repick.domain.clothingSales.validator.ClothingSalesValidator;
import com.example.repick.domain.product.repository.ProductRepository;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.aws.S3UploadService;
import com.example.repick.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.repick.global.error.exception.ErrorCode.INVALID_BOX_COLLECT_ID;
import static com.example.repick.global.error.exception.ErrorCode.USER_NOT_FOUND;

@Service @RequiredArgsConstructor
public class BoxService {

    private final UserRepository userRepository;
    private final BoxCollectRepository boxCollectRepository;
    private final BoxCollectStateRepository boxCollectStateRepository;
    private final ProductRepository productRepository;
    private final S3UploadService s3UploadService;
    private final ClothingSalesValidator clothingSalesValidator;


    @Transactional
    public BoxCollectResponse registerBoxCollect(PostBoxCollect postBoxCollect) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // BoxCollect
        BoxCollect boxCollect = postBoxCollect.toEntity(user);

        boxCollect.updateImageUrl(s3UploadService.saveFile(postBoxCollect.image(), "clothingSales/boxCollect/" + user.getId() + "/" + boxCollect.getId()));

        boxCollectRepository.save(boxCollect);

        // BoxCollectState
        BoxCollectState boxCollectState = BoxCollectState.of(BoxCollectStateType.PENDING, boxCollect);

        boxCollectStateRepository.save(boxCollectState);

        return BoxCollectResponse.of(boxCollect, boxCollectState.getBoxCollectStateType().getValue());

    }

    public GetProductListByClothingSales getProductsByBoxId(Long boxCollectId) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        BoxCollect boxCollect = boxCollectRepository.findById(boxCollectId)
                .orElseThrow(() -> new CustomException(INVALID_BOX_COLLECT_ID));

        // validate boxCollectId and user
        clothingSalesValidator.userBoxCollectMatches(user.getId(), boxCollect);

        List<GetProductByClothingSalesDto> getProductByClothingSalesDtoList = productRepository.findProductDtoByClothingSales(true, boxCollectId);

        Integer productQuantity = productRepository.countByIsBoxCollectAndClothingSalesId(true, boxCollectId);

        return new GetProductListByClothingSales(getProductByClothingSalesDtoList, boxCollect.getBoxQuantity(), productQuantity);
    }
}
