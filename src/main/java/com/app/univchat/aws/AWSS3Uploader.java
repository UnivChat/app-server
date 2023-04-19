package com.app.univchat.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AWSS3Uploader {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3Client amazonS3Client;

    /**
     *  s3 파일 업로드 메서드
     */
    public String uploadFiles(MultipartFile multipartFile, String dir) throws IOException {
        File file = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("ERR: File convert failed."));
        return upload(file, dir);

    }

    /**
     * s3 파일 삭제 메서드
     */
    public void deleteByUrl(String fileUrl) {
        String storeKey = fileUrl.replace("https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/", "");
        try {
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, URLDecoder.decode(storeKey, "UTF-8")));

        } catch (UnsupportedEncodingException e) {
            log.error("fail decoding ::: " + fileUrl + e.getMessage());
        }

    }


    // (로컬)파일 업로드
    // MultiFile -> file로 변환
    private Optional<File> convert(MultipartFile multipartFile) throws IOException {
        File convertFile = new File(System.getProperty("user.dir") + "/" + multipartFile.getOriginalFilename());
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(multipartFile.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    // s3 업로드 후 로컬 파일 삭제
    public String upload(File file, String path) {
        String name = path + "/" + UUID.randomUUID() + file.getName();
        String uploadUrl = putS3(file, name); //s3 업로드
        removeFile(file); //로컬의 파일 삭제
        return uploadUrl;
    }


    // (로컬)파일 삭제
    private void removeFile(File file) {
        if (file.delete()) {
            System.out.println("File delete succeed.");
            return;
        }
        System.out.println("File deleted failed.");
    }

    // S3 업로드
    private String putS3(File file, String name) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, name, file).withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3Client.getUrl(bucket, name).toString();
    }

}
