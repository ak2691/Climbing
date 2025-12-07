package com.allan.climberanalyzer.analyzer.service;

import java.net.MalformedURLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class S3StorageService implements StorageService {
    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    @Override
    public String saveFile(MultipartFile file, String filename) {
        String s3Key = "exercise-images/" + filename;
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return s3Key;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload to S3", e);
        }
    }

    @Override
    public Resource loadFile(String filename) {
        String s3Key = "exercise-images/" + filename;
        String url = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, s3Key);
        try {
            return new UrlResource(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to create URL for file: " + filename, e);
        }

    }

    @Override
    public void deleteFile(String filename) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from S3: " + filename, e);
        }
    }

}
