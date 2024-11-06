package com.example.imageapi.dto.Response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S3File {
    // 이미지 URL
    String url;

    // 파일명
    String name;

    // 업로드 날짜 및 시간
    String time;

    public S3File(String url, String name, String time) {
        this.url = url;
        this.name = name;
        this.time = time;
    }
}
