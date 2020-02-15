package com.example.demo.mapper;

import java.util.List;

import com.example.demo.domain.BoardAttachVO;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardAttachMapper {
    public void insert(BoardAttachVO attach);
    public List<BoardAttachVO> findByBno(Long bno);
    public void delete(String fileId);
    public void deleteAllByBno(Long bno);
    public List<BoardAttachVO> findFilesbyFolderName(String folderName);
}