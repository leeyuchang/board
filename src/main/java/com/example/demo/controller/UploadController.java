package com.example.demo.controller;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

		List<CompletableFuture<BoardAttachVO>> list = Arrays.stream(uploadFile)
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

		var emitter = emitterMap.get(sessionId);
		
		if("close".equals(event)) {
			emitter.complete();
			emitterMap.remove(sessionId);
			return;
		}

		try {
			emitterMap.get(sessionId).send(SseEmitter.event().name(event).data(attach));
		} catch (IOException e) {
			log.info("notify error on : " + e);
			emitter.completeWithError(e);
		}
    }
    
    @GetMapping("/uploadResult")
	public SseEmitter uploadResultAsync(HttpSession session) {
		
		var sessionId = session.getId();
        log.info("uploadResult started by " + sessionId);

        var emitter = new SseEmitter(600000L);
        
        emitterMap.put(sessionId, emitter);
        
        emitter.onTimeout(() -> {
            log.info("onTimeout    : " + sessionId + "/" + emitter.toString());
            emitter.complete();
        });
        
        emitter.onCompletion(() -> {
            log.info("onCompletion");
        });
        
        emitter.onError((e) -> log.info("★★★★onError★★★★" + e));
        return emitter;
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