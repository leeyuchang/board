package com.example.demo.config;

import java.net.http.HttpClient;
import java.time.Duration;

import com.example.demo.domain.AccessTokenInfo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.ApplicationScope;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public HttpClient httpClient() {
        return  HttpClient.newBuilder()
                          .version(HttpClient.Version.HTTP_2)
                          .connectTimeout(Duration.parse("PT5S"))
                          .build();
    }

    @Bean
    @ApplicationScope
    public AccessTokenInfo accessTokenInfo(){
        return new AccessTokenInfo();
    }

/*     @Bean
    public ErrorNotifier errorNotifier() {
        return new EmailErrorNotifier();
    } */
}