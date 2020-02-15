package com.example.demo.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

import com.example.demo.domain.AccessTokenDTO;
import com.example.demo.domain.RefreshTokenDTO;
import com.example.demo.mapper.AccessTokenMapper;
import com.example.demo.task.AccessTokenException;
import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccessTokenServiceImpl implements AccessTokenService {

    @Autowired
    private Gson gson;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private AccessTokenMapper accessTokenMapper;
     
    @Override
    public AccessTokenDTO get() {

        String clientId = System.getenv("GOOGLE_CLIENT_ID");
        String clientSecret = System.getenv("GOOGLE_CLIENT_SECRET");
        String clientRefreshToken = System.getenv("GOOGLE_CLIENT_REFRESH_TOKEN");
        String clientRefreshTokenUrl = System.getenv("GOOGLE_CLIENT_REFRESH_TOKEN_URL");

        Objects.requireNonNull(clientId);
        Objects.requireNonNull(clientSecret);
        Objects.requireNonNull(clientRefreshToken);
        Objects.requireNonNull(clientRefreshTokenUrl);

        RefreshTokenDTO refreshToken = RefreshTokenDTO
                                    .builder()
                                    .client_secret(clientSecret)
                                    .grant_type("refresh_token")
                                    .refresh_token(clientRefreshToken)
                                    .client_id(clientId)
                                    .build();

        String refreshTokenToJson = gson.toJson(refreshToken);

        HttpRequest request = HttpRequest
                            .newBuilder(URI.create(clientRefreshTokenUrl))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(refreshTokenToJson))
                            .build();

        HttpResponse<String> response = null;
        
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
         } catch (IOException | InterruptedException e) {
             throw new AccessTokenException(e.getCause());
        }

        return gson.fromJson(response.body(), AccessTokenDTO.class);
    }

    @Override
    public void save(AccessTokenDTO token) {
        accessTokenMapper.truncate();
        accessTokenMapper.insert(token);
    }
    
}