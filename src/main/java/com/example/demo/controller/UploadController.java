package com.example.demo.controller;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import com.example.demo.domain.BoardAttachVO;
import com.example.demo.domain.DriveFileResDTO;
import com.example.demo.service.FileUploadService;
import com.example.demo.service.UploadPathService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.java.Log;

@Log
@Controller
public class UploadController {

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private UploadPathService uploadPathService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private TaskExecutor executor;

    private Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    @PostMapping("/uploadAjax")
    public void uploadAjaxPost(MultipartFile[] uploadFile, HttpSession session) {

        log.info("uploadFile START");

        var sessionId = session.getId();

        log.info("uploadAjax started by " + sessionId);

        var folder = uploadPathService.getPath();

        var list = Arrays.stream(uploadFile)
                        .map(upFile -> CompletableFuture.supplyAsync(() -> fileUploadService.up(upFile, folder), executor)
                                                        .thenApply(dto -> converDTOtoVO(dto, folder))
                                                        .thenAccept(attach -> notify(sessionId, "message", attach))
                                                        )
                        .collect(toList());

        CompletableFuture.allOf(list.toArray(new CompletableFuture[list.size()])).join();

        // notify(sessionId, "close", BoardAttachVO.builder().build());

        log.info("uploadFile END");
    }

    private BoardAttachVO converDTOtoVO(DriveFileResDTO dto, String folder) {
        BoardAttachVO attach = new BoardAttachVO();
        attach.setFileId(dto.getId());
        attach.setFileName(dto.getName());
        attach.setFileType(dto.getMimeType().startsWith("image"));
        attach.setFolderName(folder);
        return attach;
    }

    @GetMapping("/uploadResult")
    public SseEmitter uploadResultAsync(HttpSession session) {

        var sessionId = session.getId();

        log.info("uploadResult started by " + sessionId);

        var emitter = new SseEmitter(Duration.ofMinutes(10).toMillis());

        emitterMap.put(sessionId, emitter);

        emitter.onTimeout(() -> {
            log.info("onTimeout    : " + sessionId + "/" + emitter.toString());
            emitter.complete();
        });

        emitter.onCompletion(() -> {
            log.info("onCompletion : emitterMap.before size(" + emitterMap.size() + ")");
            emitterMap.entrySet().stream()
                    .forEach((v) -> log.info("onCompletion : before " + v.getKey() + " / " + v.getValue()));

            emitterMap.remove(sessionId);
            log.info("onCompletion : Removed sessionId : " + sessionId);
            log.info("onCompletion : emitterMap.after size(" + emitterMap.size() + ")");
            emitterMap.entrySet().stream()
                    .forEach((v) -> log.info("onCompletion : after " + v.getKey() + " / " + v.getValue()));
        });

        emitter.onError((e) -> log.info("★★★★onError★★★★" + e));
        return emitter;
    }

    private synchronized void notify(String sessionId, String event, BoardAttachVO attach) {

        var emitter = emitterMap.get(sessionId);

        try {
            emitter.send(SseEmitter.event().name(event).data(attach));
        } catch (IOException e) {
            log.info("notify error on : " + e);
            emitter.completeWithError(e);
        }

        if (Objects.equals(event, "close")) {
            var emiiter = emitterMap.get(sessionId);
            if (Objects.nonNull(emiiter)) {
                emiiter.complete();
            }
        }
    }

    @GetMapping("/display")
    public ResponseEntity<byte[]> getThumbnail(String fileId) {

        log.info("display START");

        var location = UriComponentsBuilder
                                    .fromHttpUrl("https://lh3.googleusercontent.com/d/{fileId}=s220")
                                    .buildAndExpand(fileId)
                                    .toUri();

        var request = HttpRequest.newBuilder(location)
                                    .header("content-type", "application/binary")
                                    .build();

        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            return new ResponseEntity<>(response.body(), HttpStatus.OK);
        } catch (IOException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}