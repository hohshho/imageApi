package com.example.imageapi.service;

import com.example.imageapi.dto.Response.S3File;
import com.example.imageapi.util.ImageValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

// 사용자가 이미지를 업로드하면 지정된 경로를 따라 S3 버킷에 저장해야 합니다.
// 이미지는 JPEG, PNG, GIF 등의 일반적인 이미지 포맷을 지원해야 합니다.
// 업로드 시 이미지의 원본 파일 이름, S3 저장 경로, 업로드 날짜 및 시간을 서버의 콘솔에 출력하세요.
@Service("ImageService")
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private ImageValidator imageValidator;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    // 파일을 업로드 할 bucket
    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    @Value("${host.url}")
    private String host;

    @Value("${host.filepath}")
    private String path;

    private final S3Client s3Client;

    public String uploadImage(MultipartFile image) {
        imageValidator.validateImageFile(image);

        String orgFilename = image.getOriginalFilename();
        String hash = generateHash(orgFilename + getCurrentTimestamp());
        String extension = orgFilename.substring(orgFilename.lastIndexOf(".") + 1);            // 확장자
        String saveFilename = hash + "." + extension;                                              // 디스크에 저장할 파일명

        logger.info(orgFilename);
        logger.info(path);

        try {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(path + saveFilename)
                    .contentType(image.getContentType())
                    .contentLength(image.getSize())
                    .build();
            s3Client.putObject(objectRequest, RequestBody.fromBytes(image.getBytes())); // s3에 업로드
        } catch (IOException e) {
            e.printStackTrace();
        }

        return saveFilename;
    }

    public String uploadImageAddPath(String inputPath, MultipartFile image) {
        imageValidator.validateImageFile(image);

        imageValidator.validateImagePath(inputPath);

        String orgFilename = image.getOriginalFilename();
        String hash = generateHash(orgFilename + getCurrentTimestamp());
        String extension = orgFilename.substring(orgFilename.lastIndexOf(".") + 1);            // 확장자
        String saveFilename = hash + "." + extension;                                              // 디스크에 저장할 파일명

        logger.info(orgFilename);
        logger.info(inputPath);

        try {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(inputPath + saveFilename)
                    .contentType(image.getContentType())
                    .contentLength(image.getSize())
                    .build();
            s3Client.putObject(objectRequest, RequestBody.fromBytes(image.getBytes())); // s3에 업로드
        } catch (IOException e) {
            e.printStackTrace();
        }

        return saveFilename;
    }

    public byte[] getImage(String filename) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .key(filename)
                .bucket(bucketName)
                .build();

        ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(getObjectRequest);

        return responseBytes.asByteArray();
    }

    public List<S3File> getFileList() {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(path)
                .build();

        ListObjectsV2Response result;
        List<S3File> files = new LinkedList<>();

        do {
            result = s3Client.listObjectsV2(request);
            for (S3Object s3Object : result.contents()) {
                S3File file = new S3File(
                        host + "/api/getImage?filename=" + s3Object.key(),
                        s3Object.key(),
                        formatInstant(s3Object.lastModified())
                );
                files.add(file);
            }
            request = request.toBuilder()
                    .continuationToken(result.nextContinuationToken())
                    .build();
        } while (result.isTruncated());

        return files;
    }

    public String deleteImage(String key) {
        // TODO: 삭제 하려는 파일이 있는지 확인

        // DeleteObjectRequest 생성
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key) // 삭제할 파일의 키
                .build();

        // 파일 삭제
        s3Client.deleteObject(deleteRequest);

        // 삭제 확인
        return confirmDeletion(key);
    }

    //---
    private String confirmDeletion(String key) {
        try {
            // 해당 객체의 메타데이터를 가져옴
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            return "File still exists: " + key;
        } catch (NoSuchKeyException e) {
            return "File successfully deleted: " + key;
        } catch (Exception e) {
            return "Error checking file: " + e.getMessage();
        }
    }

    private String formatInstant(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // 현재 시간을 문자열로 반환하는 메서드
    private static String getCurrentTimestamp() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        return currentTime.format(formatter);
    }

    // 문자열을 해시 값으로 변환하는 메서드
    private static String generateHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            String hash = Base64.getEncoder().encodeToString(hashBytes);
            // Base64 인코딩된 문자열에서 파일명에 적합한 문자만 남기기
            hash = hash.replaceAll("[^a-zA-Z0-9]", "");
            return hash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
