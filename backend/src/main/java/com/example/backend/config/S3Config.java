package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

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

    @Bean
    public S3Client s3Client(AppProps props) {
        AwsBasicCredentials creds = AwsBasicCredentials.create(
                props.getS3().getAccessKey(),
                props.getS3().getSecretKey()
        );

        return S3Client.builder()
                .endpointOverride(URI.create(props.getS3().getEndpointUrl()))
                .region(Region.of(props.getS3().getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .forcePathStyle(true)
                .build();
    }
}