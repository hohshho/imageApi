package com.example.imageapi.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionCode {

    INVALID_REQUEST(1000, "올바르지 않은 요청입니다."),

    // 8000번대 파일 관련
    INVALID_FILE_SIZE(8001, "허용되지 않은 사이즈의 파일입니다."),
    INVALID_FILE_EXTENSION(8002, "허용되지 않은 파일 확장자입니다."),
    INVALID_FILE_PATH(8003, "허용되지 않은 경로입니다."),

    // 9000번대 서버 에러
    INTERNAL_SEVER_ERROR(9999, "서버 에러가 발생하였습니다. 관리자에게 문의해 주세요.");

    private final int code;
    private final String message;
}

