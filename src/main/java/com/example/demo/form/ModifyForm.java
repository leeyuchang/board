package com.example.demo.form;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotBlank;

import com.example.demo.domain.BoardAttachVO;

import lombok.Data;

@Data
public class ModifyForm {

    private Long bno;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotBlank
    private String writer;
    private LocalDate regdate;
    private LocalDate updateDate;
    private List<BoardAttachVO> attachList;

}