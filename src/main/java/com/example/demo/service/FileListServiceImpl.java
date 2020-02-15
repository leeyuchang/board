package com.example.demo.service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

import javax.annotation.Resource;

import com.example.demo.domain.AccessTokenInfo;
import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.java.Log;

@Log
@Service
public class FileListServiceImpl implements FileListService {

    @Resource(name = "accessTokenInfo")
    private AccessTokenInfo accessTokenInfo;

    @Autowired
    private Gson gson;

    @Autowired
    private HttpClient httpClient;

    private final String GOOGLE_DRIVE_URL = "https://www.googleapis.com/drive/v3/files?";

    @Override
    public FileListDTO getList(String folderId) {

        Objects.requireNonNull(accessTokenInfo);

        log.info("★ FileListServiceImpl ★");

        Objects.requireNonNull(folderId);

        var uri = UriComponentsBuilder.fromHttpUrl(GOOGLE_DRIVE_URL)
                .queryParam("q", "trashed=false and \"" + folderId + "\" in parents")
                .queryParam("fields", String.join(",", "files(id", "name)"))
                .build();

        var request = HttpRequest.newBuilder()
                                 .uri(uri.toUri())
                                 .header("Authorization", "Bearer " + accessTokenInfo.getAccessToken())
                                 .GET()
                                 .build();

        HttpResponse<String> res = null;

        try {
            res = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info(res.body());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (res.statusCode() == 401) {
            throw new RuntimeException("Invalid Credentials");
        }

        return gson.fromJson(res.body(), FileListDTO.class);
    }
}