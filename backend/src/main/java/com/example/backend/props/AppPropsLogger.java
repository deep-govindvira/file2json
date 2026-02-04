package com.example.backend.props;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppPropsLogger {

    private final AppProps appProps;

    public AppPropsLogger(AppProps appProps) {
        this.appProps = appProps;
    }

    @PostConstruct
    public void logProps() {
        log.info("Properties: {}", appProps);
    }
}

