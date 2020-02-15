package com.example.demo.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.annotation.PostConstruct;

import com.example.demo.domain.AccessTokenInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.java.Log;

@Log
@Service
public class DriveFileDeleteImpl implements DriveFileDelete {

    // @Resource(name = "accessTokenInfo")
    private static AccessTokenInfo accessTokenInfo = new AccessTokenInfo();

    @Autowired
    private HttpClient client;

    @PostConstruct
    public void setup() {
        accessTokenInfo.setAccessToken(
            "ya29.ImC8B7PPhB9GACz4pa0LGlGJeu6f535C4IFhmAIhkOniR6J5d36cO6-kBSFoGXBThFbRIM2cfbVJcDmKZdMZS3933IfBjxJQnWYnLv8kVNy2_anbxT0VQUEOSrb24vSi4kw");
    }
    
    /**
     * Google Driveから指定のファイルを削除する。 削除失敗時には {@link FileDeletedException}を返す。
     * 
     * @throws FileDeletedException
     */
    @Override
    public void remove(String fileId) throws FileDeletedException {

        var httpRequest = HttpRequest.newBuilder(URI.create("https://www.googleapis.com/drive/v3/files/" + fileId))
                                        .header("Authorization", "Bearer " + accessTokenInfo.getAccessToken())
                                        .DELETE()
                                        .build();
        try {
            client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.info(e.getMessage());
            throw new FileDeletedException("Failure on removing the file : " + fileId);
        }
    }

}