package com.example.repick.domain.clothingSales.api;

import com.example.repick.domain.clothingSales.dto.PostRequestDto;
import com.example.repick.domain.clothingSales.service.ClothingSalesService;
import com.example.repick.domain.clothingSales.dto.PostRequestDto;
import com.example.repick.global.aws.S3UploadService;
import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.error.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import java.io.IOException;

import static com.amazonaws.services.ec2.model.ResourceType.Image;

@RestController
@RequestMapping("/clothing-sales")
public class ClothingSalesController {

    private final ClothingSalesService clothingSalesService;
    private final S3UploadService s3UploadService;
    private final UserRepository userRepository;

    @Autowired
    public ClothingSalesController(ClothingSalesService clothingSalesService,
                                   S3UploadService s3UploadService,
                                   UserRepository userRepository) {
        this.userRepository = userRepository;
        this.clothingSalesService = clothingSalesService;
        this.s3UploadService = s3UploadService;
    }

    @PostMapping(value = "/details", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void post(@ModelAttribute PostRequestDto postRequestDto) throws IOException {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        String url = s3UploadService.saveFile(postRequestDto.getFile(), "clothingSales/" + user.getId());
        clothingSalesService.save(postRequestDto, url, user.getId());
    }
}

