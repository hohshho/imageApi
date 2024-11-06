package com.example.imageapi.controller;

import com.example.imageapi.dto.Response.S3File;
import com.example.imageapi.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ImageController {
    private final ImageService imageService;

    /**
     * 에디터 이미지 업로드
     *
     * @param image 파일 객체
     * @return 업로드 된 파일 명
     */
    @PostMapping("/upload")
    public String uploadImage(@RequestParam final MultipartFile image) {
        return imageService.uploadImage(image);
    }

    /**
     * 에디터 이미지 업로드
     *
     * @param image 파일 객체
     * @return 업로드 된 파일 명
     */
    @PostMapping("/upload/addFilePath")
    public String uploadImageAndAddFilePath(@RequestParam final String path, @RequestParam final MultipartFile image) {
        return imageService.uploadImageAddPath(path, image);
    }

    /**
     * 에디터 이미지 리스트 조회
     *
     * @return 업로드 된 파일 리스트
     */
    @GetMapping("/getFileList")
    public List<S3File> getFileList() {
        return imageService.getFileList();
    }

    /**
     * 디스크에 업로드된 파일을 byte[]로 반환
     * @param filename 디스크에 업로드된 파일명
     * @return image byte array
     */
    @GetMapping(value = "/getImage", produces = { MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE })
    public byte[] getImage(@RequestParam String filename) {
        return imageService.getImage(filename);
    }

    /**
     * 에디터 이미지 리스트 조회
     *
     * @return 업로드 된 파일 리스트
     */
    @GetMapping("/deleteImage")
    public String deleteImage(@RequestParam final String filename) {
        return imageService.deleteImage(filename);
    }
}
