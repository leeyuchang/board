package com.example.demo.domain;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class BoardVO {
    private Long bno;
    private String title;
    private String content;
    private String writer;
    private LocalDate regdate;
    private LocalDate updateDate;
    private int replyCnt;
    private List<BoardAttachVO> attachList;
}