package com.simon.scheduledawg;

import org.flywaydb.core.Flyway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ScheduleDawgApplication {

	public static void main(String[] args) {
		runMigrations();
		SpringApplication.run(ScheduleDawgApplication.class, args);
	}

	// Spring Boot 4.1's autoconfigure module doesn't include Flyway support
	// (no FlywayAutoConfiguration on the classpath), so migrations are run here
	// directly, before the Spring context — and therefore before Hibernate's
	// ddl-auto=validate — starts up. Keep these defaults in sync with the
	// spring.datasource.* ones in application.properties.
	private static void runMigrations() {
		String url = envOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/scheduledawg");
		String user = envOrDefault("DB_USERNAME", "scheduledawg_user");
		String password = envOrDefault("DB_PASSWORD", "scheduledawg123");

		Flyway.configure()
				.dataSource(url, user, password)
				.baselineOnMigrate(true)
				.baselineVersion("1")
				.locations("classpath:db/migration")
				.load()
				.migrate();
	}

	private static String envOrDefault(String name, String defaultValue) {
		String value = System.getenv(name);
		return (value == null || value.isBlank()) ? defaultValue : value;
	}

}
