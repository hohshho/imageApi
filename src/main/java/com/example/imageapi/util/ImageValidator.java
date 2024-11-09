package com.example.imageapi.util;

import com.example.imageapi.global.exception.BadRequestException;
import com.example.imageapi.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class ImageValidator {
    // 파일 유효성 검사 메서드
    public static void validateImageFile(MultipartFile file) {
        // 파일 크기 검사
        validateFileSize(file);

        // 파일 확장자 검사
        validateFileExtension(file);
    }

    // 파일 경로 확인
    public static String validateImagePath(String path){
        validateStartPath(path);

        validateParentDirectory(path);

        validatePathDepth(path);

        return null;
    }

    // 파일 객체의 크기를 확인 (5MB 이상인 경우 에러 반환)
    private static void validateFileSize(MultipartFile file) {
        long maxSizeBytes = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSizeBytes) {
            throw new BadRequestException(ExceptionCode.INVALID_FILE_SIZE);
        }
    }

    // 저장 가능한 이미지 파일 확장자 여부 확인
    private static void validateFileExtension(MultipartFile file) {
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "tif", "tiff", "webp", "svg");
        String fileName = file.getOriginalFilename();
        if (fileName != null) {
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            if (!allowedExtensions.contains(extension)) {
                throw new BadRequestException(ExceptionCode.INVALID_FILE_EXTENSION);
            }
        }
    }

    // 상위 디렉토리 포함 여부 확인
    private static void validateParentDirectory(String path){
        if(path.contains("..")) {
            throw new BadRequestException(ExceptionCode.INVALID_FILE_PATH);
        }
    }

    // 너무 깊은 depth요청 시 에러 (4이상)
    private static void validatePathDepth(String path){
        String[] checkPath = path.split("/");

        if(checkPath.length > 4) {
            throw new BadRequestException(ExceptionCode.INVALID_FILE_PATH);
        }
    }

    private static void validateStartPath(String path) {
        if (path != null && (path.startsWith(".") || path.startsWith("/"))) {
            throw new BadRequestException(ExceptionCode.INVALID_FILE_PATH);
        }
    }

}

