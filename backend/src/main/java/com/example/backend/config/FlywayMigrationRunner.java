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
        // Build JDBC URL from AppProps
        String jdbcUrl = "jdbc:postgresql://"
                + appProps.getSpring().getDatasource().getHost() + ":"
                + appProps.getSpring().getDatasource().getPort() + "/"
                + appProps.getSpring().getDatasource().getName();

        Flyway flyway = Flyway.configure()
                .dataSource(
                        jdbcUrl,
                        appProps.getSpring().getDatasource().getUsername(),
                        appProps.getSpring().getDatasource().getPassword()
                )
                .locations("classpath:db/migration") // your migration scripts
                .baselineOnMigrate(true)             // safe for existing DBs
                .validateOnMigrate(true) // <-- checks DB against migration history
                .load();

        flyway.migrate();

    }
}