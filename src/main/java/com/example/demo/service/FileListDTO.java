package com.example.demo.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import groovy.transform.ToString;

@ToString
public class FileListDTO {

    private List<Files> files;

    public FileListDTO(List<Files> files) {
        this.files = files;
    }

    public List<Files> asList() {
        Objects.requireNonNullElseGet(files, () -> Collections.emptyList());
        return List.copyOf(files);
    }

}
