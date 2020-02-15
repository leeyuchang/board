package com.example.demo.task;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.example.demo.config.SchedulingConfiguration;
import com.example.demo.mapper.BoardAttachMapper;
import com.example.demo.mapper.UploadPathMapper;
import com.example.demo.service.FileDeleteService;
import com.example.demo.service.FileDeletedException;
import com.example.demo.service.FileListService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.java.Log;

/**
 * 毎日午前２時に起動し、DBに存在しないファイルをGoogle Driveから削除
 * @see SchedulingConfiguration.java
 */
@Log
@Component
public class FileDeleteTask {

    @Autowired
    UploadPathMapper uploadPathMapper;

    @Autowired
    BoardAttachMapper boardAttachMapper;
    
    @Autowired
    FileListService fileListWebService;

    @Autowired
    FileDeleteService fileDeleteWebService;

    @Scheduled(cron = "0 0 2 * * *")
    public void deleteFiles() {

        log.info("★File Delete Task run...");
        
        /**
         * １．作業対象取得
         * 　　※0件処理は後回し
         */
        // 昨日のフォルダーを所得
        var path = uploadPathMapper.getYesterDayFolder();
        // DBから昨日フォルダー配下のFileListを取得
        var db = boardAttachMapper.findFilesbyFolderName(path.getDay());
        // Google Driveから昨日フォルダ配下のFileListを取得
		var drive = fileListWebService.getList(path.getDay());

        /**
         * ２．変換処理
         */
        // DBのFileListからFileIdを抽出してセットを作成
		var driveFileIds = drive.asList().stream()
										.map(o -> o.getId())
                                        .collect(toSet());
        // Google DriveのFileListからFileIdを抽出してセットを作成
		var dbFileIds = db.stream()
						 .map(o -> o.getFileId())
						 .collect(toSet());

        /**
         * ３．削除対象特定
         */
        // Google Driveの削除対象リストを作成
		var deleteListFromDrive = driveFileIds.stream()
											  .filter(id -> !dbFileIds.contains(id))
											  .collect(toList());

        deleteListFromDrive.stream().forEach(e -> log.info("To be deleted : " + e));

        /**
         * ４．削除実施
         */
        List<CompletableFuture<Void>> list = null;
        try {
            list = deleteListFromDrive.parallelStream()
                    .map(id -> CompletableFuture.runAsync(() -> fileDeleteWebService.delete(id)))
                    .collect(toList());

        } catch (FileDeletedException e) {
            log.info(e.getMessage());
        }

        CompletableFuture.allOf(list.toArray(new CompletableFuture[list.size()])).join();

    }
}