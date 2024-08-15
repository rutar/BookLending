package com.example.booklending;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresTestContainer {

    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    static {
        container.start();

        // Register a shutdown hook to stop the container when the JVM exits
        Runtime.getRuntime().addShutdownHook(new Thread(container::stop));
    }

    public static PostgreSQLContainer<?> getInstance() {
        return container;
    }
}
