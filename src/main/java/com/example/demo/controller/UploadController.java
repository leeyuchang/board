package com.example.demo.controller;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import com.example.demo.domain.BoardAttachVO;
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
    public ResponseEntity<List<BoardAttachVO>> uploadAjaxPost(MultipartFile[] uploadFile, HttpSession session) {

        var sessionId = session.getId();
        log.info("start /upload with session : " + sessionId);

        long startTime = System.nanoTime();

        var folderId = uploadPathService.getPath();

        var list = Arrays.stream(uploadFile)
                .map(up -> CompletableFuture.supplyAsync(() -> fileUploadService.up(up, folderId), executor))
                .collect(Collectors.toList());

        list.parallelStream()
            .map(CompletableFuture::join)
            .forEach(vo -> notify(sessionId, "message", vo));

        long endTime = (System.nanoTime() - startTime) / 1_000_000;
        log.info("★ time cost : " + endTime + "msecs");

        notify(sessionId, "close", new BoardAttachVO());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void notify(String sessionId, String event, BoardAttachVO attach) {

        List<String> deadEmitters = new ArrayList<>();
        emitterMap.forEach((k, v) -> {
            if (k.equals(sessionId)) {
                try {
                    v.send(SseEmitter.event().name(event).data(attach));
                } catch (IOException e) {
                    deadEmitters.add(k);
                }
            }
        });
        deadEmitters.stream().forEach(emiiter -> emitterMap.remove(emiiter));
    }

    @GetMapping("/uploadResult")
    public SseEmitter uploadResultAsync(HttpSession session) {

        var sessionId = session.getId();
        log.info("uploadResult started by " + sessionId);

        var emitter = new SseEmitter(0L);

        emitterMap.put(sessionId, emitter);

        emitter.onTimeout(() -> {
            emitter.complete();
            log.info("onTimeout    : " + sessionId + "/" + emitter.toString());
        });

        emitter.onCompletion(() -> {
            synchronized (this) {
                emitterMap.remove(session, emitter);
            }
            log.info("onCompletion : " + sessionId + "/" + emitter.toString());
        });

        emitter.onError((e) -> log.info("onError      : " + sessionId + "/" + emitter.toString()));
        return emitter;
    }

    @GetMapping("/display")
    public ResponseEntity<byte[]> getThumbnail(String fileId) {

        log.info("display START");

        var location = UriComponentsBuilder.fromHttpUrl("https://lh3.googleusercontent.com/d/{fileId}=s220")
                                           .buildAndExpand(fileId).toUri();

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