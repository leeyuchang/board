package com.example.demo.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BoardAttachVO {
    private String fileId;
    private String fileName;
    private boolean fileType;
    private String folderName;
    private Long bno;
}