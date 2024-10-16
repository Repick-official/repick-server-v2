package com.example.repick.global.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.repick.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.example.repick.global.error.exception.ErrorCode.IMAGE_UPLOAD_FAILED;


@Service @RequiredArgsConstructor
public class S3UploadService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String saveFile(MultipartFile multipartFile, String path) {
        try {
            String originalFilename = multipartFile.getOriginalFilename();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());

            String filename = LocalDateTime.now() + "_" + originalFilename;

            amazonS3.putObject(bucket + "/" + path, filename, multipartFile.getInputStream(), metadata);
            return amazonS3.getUrl(bucket + "/" + path, filename).toString();
        }
        catch (IOException e) {
            throw new CustomException(IMAGE_UPLOAD_FAILED);
        }

    }

}
