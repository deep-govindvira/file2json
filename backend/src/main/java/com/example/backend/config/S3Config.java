package com.example.backend.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

//    @Bean
//    public S3Client s3Client() {
//
//        AwsBasicCredentials creds = AwsBasicCredentials.create("test", "test");
//
//        return S3Client.builder()
//                .endpointOverride(URI.create("http://localhost:4566")) // LocalStack
//                .region(Region.US_EAST_1)
//                .credentialsProvider(StaticCredentialsProvider.create(creds))
//                .forcePathStyle(true)
//                .build();
//    }

    //    @Bean
//    public S3Client s3Client() {
//        AwsBasicCredentials creds = AwsBasicCredentials.create(
//                "admin",
//                "admin123"
//        );
//
//        return S3Client.builder()
//                .endpointOverride(URI.create("http://localhost:9000"))
//                .region(Region.US_EAST_1)
//                .credentialsProvider(StaticCredentialsProvider.create(creds))
//                .forcePathStyle(true)
//                .build();
//    }
}