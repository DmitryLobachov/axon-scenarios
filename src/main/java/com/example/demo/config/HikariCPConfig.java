package com.example.demo.config;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HikariCPConfig {

	@Value("${hikari.driver-class-name}")
    String driverClassName;

	@Value("${hikari.jdbc.url}")
    String jdbcUrl;

	@Value("${hikari.username}")
    String username;

	@Value("${hikari.password}")
    String password;

	@Value("${hikari.pool.size}")
	int poolSize;

	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		HikariDataSource ds = new HikariDataSource();

	// init
		ds.setMaximumPoolSize(poolSize);
		ds.setDriverClassName(driverClassName);
		ds.setJdbcUrl(jdbcUrl);
		ds.setUsername(username);
		ds.setPassword(password);

	// hardcoded values
		ds.setMinimumIdle(5);
		ds.setConnectionTimeout(5 * 60 * 1000);
		ds.setConnectionInitSql("/* ping */ SELECT 1");
		ds.setLeakDetectionThreshold(0);
		return ds;
	}
}
