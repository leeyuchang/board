package com.example.demo.service;

import java.util.List;

import com.example.demo.domain.BoardAttachVO;
import com.example.demo.domain.BoardVO;
import com.example.demo.domain.Criteria;

public interface BoardService {
    public int register(BoardVO board);
    public BoardVO get(Long bno);
    public boolean modify(BoardVO board);
    public int remove(Long bno);
    public List<BoardVO> getList(Criteria cri);
    public int getTotol(Criteria cri);
    public int updateReplyCnt(Long bno);
    public List<BoardAttachVO> getAttachList(Long bno);
}