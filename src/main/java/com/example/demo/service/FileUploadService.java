package com.example.demo.service;

import com.example.demo.domain.DriveFileResDTO;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    public DriveFileResDTO up(MultipartFile file, String folderId);
}