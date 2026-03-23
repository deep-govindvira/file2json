package com.example.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "")
@Data
public class AppProps {
    private Spring spring;
    private Server server;
    private String uploadPath;
    private String processApiUrl;
    private String noOfThreads;
    private String allowedOrigin;
    private S3 s3;

    @Data
    public static class S3 {
        private String endpointUrl;
        private String accessKey;
        private String secretKey;
        private String region;
        private String bucket;
    }

    @Data
    public static class Spring {
        private Application application;
        private Servlet servlet;
        private Threads threads;
        private Datasource datasource;
        private Mail mail;
    }

    @Data
    public static class Mail {
        private String username;
    }

    @Data
    public static class Datasource {
        private String url;
        private String username;
        private String password;
        private String driverClassName;
    }


    @Data
    public static class Application {
        private String name;
    }

    @Data
    public static class Servlet {
        private Multipart multipart;
    }

    @Data
    public static class Multipart {
        private String maxFileSize;
        private String maxRequestSize;
        private String maxFileCount;
    }

    @Data
    public static class Threads {
        private Virtual virtual;
    }

    @Data
    public static class Virtual {
        private boolean enabled;
    }

    @Data
    public static class Server {
        private int port;
    }
}
