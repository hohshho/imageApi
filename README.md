프로젝트 설명

## 1. 프로젝트 구조

```text
root
├─  build : 빌드 시 빌드된 파일 생성
├─  src
│    ├─ main 
│    │    └─ com.example.imageapi 
│    │        ├─ controller : controller 모음
│    │        ├─ dto        : request, response시 필요한 data 객체 모음
│    │        ├─ global
│    │        │     ├─ config     : S3 설정
│    │        │     └─ exception  : exception처리 모음
│    │        ├─ service    : service(비즈니스 로직 처리) 모음
│    │        └─ util       : util 모음
│    └─ resources 
│          ├─ application.yml           : application에 필요한 설정 값
│          └─ application-security.yml  : aws 설정 값
└─  test
     └─ java  : icon 폴더
         └─ com.example.imageapi 
             └─ imageApiApplicationTest : test작성 코드 
```

## 2. API사용 방법

1) 빌드 명령어 실행

```bash
./gradlew build
```

2) web.jar실행
```bash
java -jar ./build/libs/web.jar 
```

## 3. API 경로

### 1. localhost:8099/api/upload
> 이미지 업로드

### 2. localhost:8099/api/upload/addFilePath
> 파일 경로를 추가해 이미지 업로드

### 3. localhost:8099/api/getFileList
> 전체 이미지 json로 호출

### 4. localhost:8099/api/getImage?filename=local/{파일 이름}
> 호출이 가능한 이미지 경로 URL 반환

### 5. localhost:8099/api/deleteImage?filename={파일 이름}
> 이미지 삭제


## 4. 기능 리스트

이미지 업로드 및 관리 API

1. 이미지 업로드 API
- 사용자가 이미지를 업로드하면 지정된 경로를 따라 S3 버킷에 저장
- 이미지는 JPEG, PNG, GIF 등의 일반적인 이미지 포맷을 지원
- 이미지 파일 크기 제한: 업로드 가능한 이미지 파일 크기를 5MB로 제한
- 경로별 관리: 사용자가 지정한 경로에 따라 이미지를 S3에 업로드
- 업로드 시 확장자 체크, 파일 크기 체크

2. 이미지 조회 API
- S3 버킷에 저장된 모든 이미지의 정보를 반환하는 API를 구현
- 각 이미지는 이미지 URL, 파일명, 업로드 날짜 및 시간 등의 정보를 포함

3. 이미지 삭제 API
- 이미지의 파일명을 입력으로 받아 해당 이미지를 S3 버킷에서 삭제하는 API를 구현
- 삭제 성공 또는 실패 여부를 JSON 형태로 응답