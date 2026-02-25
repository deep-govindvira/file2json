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

    @Data
    public static class Spring {
        private Application application;
        private Servlet servlet;
        private Threads threads;
        private Datasource datasource;
    }

    @Data
    public static class Datasource {
        private String host;
        private int port;
        private String name;
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
