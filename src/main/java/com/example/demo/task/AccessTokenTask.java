package com.example.demo.task;

import javax.annotation.Resource;

import com.example.demo.domain.AccessTokenDTO;
import com.example.demo.domain.AccessTokenInfo;
import com.example.demo.service.AccessTokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.java.Log;

@Log
@Component
public class AccessTokenTask {

    @Resource(name = "accessTokenInfo")
    private AccessTokenInfo accessTokenInfo;

    @Autowired
    private AccessTokenService accessTokenService;

    // Duration.ofHours(1).toMillis();
    @Scheduled(fixedRate = 3600000)
    public void execute() {

        log.info("Google Drive AccessToken Task Start");

        try {

            // Google Drive AccessToken取得
            AccessTokenDTO token = accessTokenService.get();

            // Google Drive AccessToken保存
            accessTokenService.save(token);

            // 取得したAccessTokenをApplication Scopeに格納
            accessTokenInfo.setAccessToken(token.getAccessToken());

        } catch (AccessTokenException e) {

            // Email発送
            // errorNotifier.notifySystemError(new Email("DemoApplication"), // 送信
            //         e.getMessage(), // タイトル
            //         new Email("leeyucha@gmail.com"), // 受信
            //         new Content("text/plain", e.getMessage())) // 内容
            // ;
            
            log.info(e.getCause().toString());
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }

    }

}