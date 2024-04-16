package com.example.repick.domain.advertisement.service;

import com.example.repick.domain.advertisement.dto.AdvertisementResponse;
import com.example.repick.domain.advertisement.dto.PostAdvertisement;
import com.example.repick.domain.advertisement.entity.Advertisement;
import com.example.repick.domain.advertisement.repository.AdvertisementRepository;
import com.example.repick.global.aws.S3UploadService;
import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service @RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final S3UploadService s3UploadService;

    public AdvertisementResponse postAdvertisement(PostAdvertisement postAdvertisement) {

        Advertisement advertisement = postAdvertisement.toAdvertisement();

        advertisementRepository.findBySequence(advertisement.getSequence())
                .ifPresent(ad -> {
                    throw new CustomException(ErrorCode.ADVERTISEMENT_SEQUENCE_DUPLICATED);
                });

        try {
            String imageUrl = s3UploadService.saveFile(postAdvertisement.image(), "advertisement/" + advertisement.getId());
            advertisement.updateImageUrl(imageUrl);
            advertisementRepository.save(advertisement);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }

        return AdvertisementResponse.of(advertisement);

    }
}
