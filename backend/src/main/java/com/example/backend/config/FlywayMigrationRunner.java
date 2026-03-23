package com.example.backend.config;

import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FlywayMigrationRunner {

    private final AppProps appProps;

    @EventListener(ApplicationReadyEvent.class)
    public void migrateDatabase() {

        String jdbcUrl = appProps.getSpring().getDatasource().getUrl();

        Flyway flyway = Flyway.configure()
                .dataSource(
                        jdbcUrl,
                        appProps.getSpring().getDatasource().getUsername(),
                        appProps.getSpring().getDatasource().getPassword()
                )
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .validateOnMigrate(true)
                .load();

        flyway.migrate();
    }

}