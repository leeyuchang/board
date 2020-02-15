package com.example.demo.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import javax.annotation.Resource;

import com.example.demo.domain.AccessTokenInfo;
import com.example.demo.domain.DriveFileResDTO;
import com.example.demo.domain.MetaData;
import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.java.Log;

@Log
@Service
public class FileUploadServiceImpl implements FileUploadService {

        // private AccessTokenInfo accessTokenInfo = new AccessTokenInfo();

        @Resource(name = "accessTokenInfo")
        private AccessTokenInfo accessTokenInfo;

        @Autowired
        private Gson gson;

        @Autowired
        private HttpClient httpClient;

        private final String GOOGLE_DRIVE_URL = "https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart";

        @Override
        public DriveFileResDTO up(MultipartFile file, String folderId) {

                Objects.requireNonNull(accessTokenInfo);

                log.info("★ filename : " + file.getOriginalFilename());
                
                Objects.requireNonNull(file);
                Objects.requireNonNull(folderId);

                UriComponents paramBuilder = UriComponentsBuilder.fromHttpUrl(GOOGLE_DRIVE_URL)
                                // .queryParam("fields", String.join(",","webContentLink"))
                                .build();

                // 固定文
                final String boundary = "-------314159265358979323846";
                final String delimiter = "\r\n--" + boundary + "\r\n";
                final String closeDelim = "\r\n--" + boundary + "--";

                MetaData metaData = MetaData.builder()
                                            .name(file.getOriginalFilename())
                                            .mimeType(file.getContentType())
                                            .parents(Arrays.asList(folderId))
                                            .build();

                String base64FileData = "";
                try {
                        base64FileData = Base64.getEncoder().encodeToString(file.getBytes());
                } catch (IOException e) {
                        throw new RuntimeException(e);
                }

                StringBuffer stringBuffer = new StringBuffer();

                String multipartRequestBody = stringBuffer.append(delimiter)
                                .append("content-type: application/json; charset=UTF-8\r\n\r\n")
                                .append(gson.toJson(metaData)).append(delimiter)
                                .append("content-transfer-encoding: base64\r\n")
                                .append("content-type: " + file.getContentType())
                                .append("\r\n\r\n").append(base64FileData).append(closeDelim).toString();

                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(paramBuilder.toUriString()))
                                .header("Authorization", "Bearer " + accessTokenInfo.getAccessToken())
                                .header("Content-Type", "multipart/related; boundary=\"" + boundary + "\"")
                                .POST(BodyPublishers.ofString(multipartRequestBody)).build();

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

                return gson.fromJson(res.body(), DriveFileResDTO.class);
    }

}