package com.example.demo.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReplyVO {
    private Long rno;
    private Long bno;

    private String reply;
    private String replyer;
    private LocalDateTime replyDate;
    private LocalDateTime updateDate;
}