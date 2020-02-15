package com.example.demo.service;

public interface DriveFileDelete {

    public void remove(String fileId) throws FileDeletedException;
}