package com.example.demo.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.example.demo.domain.AccessTokenInfo;
import com.example.demo.domain.Date;
import com.example.demo.domain.DriveFileResDTO;
import com.example.demo.domain.MetaData;
import com.example.demo.domain.UploadPathVO;
import com.example.demo.mapper.UploadPathMapper;
import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.java.Log;

/**
 * Google Driveに日付型のフォルダを作成
 * 例）2020-02-01
 * \---2020
 *     \---02
 *         \---01
 * 
 * @author 李 儒昌(leeyucha@gmail.com)
 * @version 0.1
 * @since 1.0
 */
@Log
@Service
public class UploadPathServiceImpl implements UploadPathService {

    @Resource(name = "accessTokenInfo")
    private AccessTokenInfo accessTokenInfo;

    @Autowired
    private Gson gson;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private UploadPathMapper uploadPathMapper;

    private Optional<UploadPathVO> uploadPathCache;

    // ルートパス名 (upload)
    private static final String ROOT = "1eC9veHgZjYVY1xacm8l8rpNrXZF0z-Ho";

    @PostConstruct
    public void setup() {
        // accessTokenInfo.setAccessToken("");
        Objects.requireNonNull(accessTokenInfo);

        // DBからデータを取得し、Cacheへ設定
        uploadPathCache = Optional.ofNullable(uploadPathMapper.select());

        // 0件の場合はデータを作成してCacheへ設定
        if (uploadPathCache.isEmpty()) {

            Date today = new Date(LocalDate.now());
            String todayArr[] = today.getDateArr();
            String newValues[] = new String[4];
    
            newValues[0] = ROOT; // rootは固定
            try {
                for (int i = 0; i < todayArr.length; i++) {
                    newValues[i + 1] = makeFolder(todayArr[i], newValues[i]);
                }
            } catch (FolderCreationException e) {
                throw new RuntimeException(e.getMessage());
            }

            UploadPathVO uploadPathVO = new UploadPathVO(
                    LocalDateTime.now(), // id
                    newValues[0], // root
                    newValues[1], // year
                    newValues[2], // month
                    newValues[3] // day
            );
            insertDB(uploadPathVO);
            setUploadPathCache(uploadPathVO);
        }
    }

    /**
     * DB Insert
     * @param uploadPathVO
     */
    @Transactional
    private void insertDB(UploadPathVO uploadPathVO) {
        // uploadPathMapper.truncate();
        uploadPathMapper.insert(uploadPathVO);
    }

    /**
     * Google Driveの格納先を返す。
     */
    @Override
    public String getPath() {

        // ★Optionalを使った方が良かったか？
        UploadPathVO cache = uploadPathCache
                            .orElseThrow(() -> new NullPointerException("tbl_upload_path 0件"));

        // Cacheが最新状態であれば、Cacheからデータを返して処理を終了
        if (cache.isToday()) {
            return cache.getDay();
        }

        Date today = new Date(LocalDate.now());

        final String todayArr[] = today.getDateArr();
        final String olddayArr[] = cache.getDateArr();
        String oldValues[] = cache.getValues();
        String newValues[] = new String[4];

        newValues[0] = oldValues[0]; // rootは固定
        for (int i = 0; i < todayArr.length; i++) {
            if (todayArr[i].contentEquals(olddayArr[i]) ) {
                newValues[i + 1] = oldValues[i + 1];
            } else {
                newValues[i + 1] = makeFolder(todayArr[i], oldValues[i]);
            }
        }

        UploadPathVO uploadPathVO = new UploadPathVO(
                LocalDateTime.now(), // DB自動採番
                newValues[0], // root
                newValues[1], // year
                newValues[2], // month
                newValues[3] // day
        );

        // DB挿入
        insertDB(uploadPathVO);

        // Cacheを更新する
        setUploadPathCache(uploadPathVO);

        return getUploadPathCache();
    }

    /**
     * Google Driveの指定場所にフォルダを作成しフォルダIDを返す。
     * 
     * @param folderName フォルダ名
     * @param parents フォルダの作成位置
     * @return フォルダID
     */
    private String makeFolder(final String folderName, final String parents) {

        MetaData mataData = MetaData
                            .builder()
                            .name(folderName)
                            .mimeType("application/vnd.google-apps.folder")
                            .parents(Arrays.asList(parents))
                            .build();

        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create("https://www.googleapis.com/drive/v3/files"))
                                             .header("Content-Type", "application/json")
                                             .header("Authorization", "Bearer " + accessTokenInfo.getAccessToken())
                                             .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(mataData)))
                                             .build();

        HttpResponse<String> response = null;

        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            log.info(response.body());
            if (response.statusCode() == 404) {
                throw new FolderCreationException("folder not found : " + folderName);
            }
        } catch (IOException | InterruptedException e) {
            throw new FolderCreationException(e.getMessage());
        }

        return gson.fromJson(response.body(), DriveFileResDTO.class).getId();
    }

    private void setUploadPathCache(UploadPathVO vo) {
        uploadPathCache = Optional.ofNullable(vo);
    }

    private String getUploadPathCache() {
        return uploadPathCache.orElseThrow(()->new NullPointerException("tbl_upload_path 0件")).getDay();
    }

}