package com.example.demo.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

import javax.annotation.Resource;

import com.example.demo.domain.AccessTokenInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.java.Log;

@Log
@Service
public class FileDeleteServiceImpl implements FileDeleteService {

    @Autowired
    private HttpClient httpClient;

    @Resource(name = "accessTokenInfo")
    private AccessTokenInfo accessTokenInfo;

    @Override
    public void delete(String fileId) {

        Objects.requireNonNull(fileId);

        HttpRequest httpRequest = HttpRequest
                .newBuilder(URI.create("https://www.googleapis.com/drive/v3/files/" + fileId))
                .header("Authorization", "Bearer " + accessTokenInfo.getAccessToken())
                .DELETE()
                .build();

        HttpResponse<String> response = null;

        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.info(e.getMessage());
            throw new FileDeletedException(e.getMessage());
        }

        if (response.statusCode() < 200 || response.statusCode() > 299 ) {
            throw new FileDeletedException("Exception on deleting the fileId : " + fileId);
        }
    }

}