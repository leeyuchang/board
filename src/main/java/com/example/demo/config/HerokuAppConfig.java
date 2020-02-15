package com.example.demo.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import lombok.extern.java.Log;

@Log
@Configuration
public class HerokuAppConfig {

     @Bean
    public DataSource dataSource() {

        String database_url = System.getenv("DATABASE_URL");
        Objects.requireNonNull(database_url);
        log.info("★ DATABASE_URL :" + database_url);

        URI dbUri = null;
        try {
            dbUri = new URI(database_url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }

}