package com.example.demo.service;

import com.example.demo.domain.BoardAttachVO;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    public BoardAttachVO up(MultipartFile file, String folderId);
}