package com.example.repick.domain.advertisement.service;

import com.example.repick.domain.advertisement.dto.AdvertisementResponse;
import com.example.repick.domain.advertisement.dto.PatchAdvertisement;
import com.example.repick.domain.advertisement.dto.PostAdvertisement;
import com.example.repick.domain.advertisement.entity.Advertisement;
import com.example.repick.domain.advertisement.repository.AdvertisementRepository;
import com.example.repick.global.aws.S3UploadService;
import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service @RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final S3UploadService s3UploadService;

    private void uploadAndUpdateImage(MultipartFile image, Advertisement advertisement) {
        try {
            String imageUrl = s3UploadService.saveFile(image, "advertisement/" + advertisement.getId());
            advertisement.updateImageUrl(imageUrl);
            advertisementRepository.save(advertisement);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    public AdvertisementResponse postAdvertisement(PostAdvertisement postAdvertisement) {
        Advertisement advertisement = postAdvertisement.toAdvertisement();

        advertisementRepository.findBySequence(advertisement.getSequence())
                .ifPresent(ad -> {
                    throw new CustomException(ErrorCode.ADVERTISEMENT_SEQUENCE_DUPLICATED);
                });

        uploadAndUpdateImage(postAdvertisement.image(), advertisement);

        return AdvertisementResponse.of(advertisement);

    }

    public AdvertisementResponse patchAdvertisement(PatchAdvertisement patchAdvertisement) {
        Advertisement advertisement = advertisementRepository.findById(patchAdvertisement.id())
                .orElseThrow(() -> new CustomException(ErrorCode.ADVERTISEMENT_NOT_FOUND));


        if (patchAdvertisement.sequence() != null) {
            advertisementRepository.findBySequence(patchAdvertisement.sequence())
                    .ifPresent(ad -> {
                        throw new CustomException(ErrorCode.ADVERTISEMENT_SEQUENCE_DUPLICATED);
                    });
        }

        advertisement.updateSequence(patchAdvertisement.sequence());

        advertisementRepository.save(advertisement);

        return AdvertisementResponse.of(advertisement);

    }

    public Boolean deleteAdvertisement(Long advertisementId) {
        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new CustomException(ErrorCode.ADVERTISEMENT_NOT_FOUND));

        advertisementRepository.delete(advertisement);

        return true;
    }

    public List<AdvertisementResponse> getAdvertisementList() {
        return AdvertisementResponse.ofList(advertisementRepository.findAllByOrderBySequenceAsc());
    }
}
