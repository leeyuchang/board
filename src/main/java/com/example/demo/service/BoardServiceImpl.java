package com.example.demo.service;

import java.util.List;

import com.example.demo.domain.BoardAttachVO;
import com.example.demo.domain.BoardVO;
import com.example.demo.domain.Criteria;
import com.example.demo.mapper.BoardAttachMapper;
import com.example.demo.mapper.BoardMapper;
import com.example.demo.mapper.ReplyMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.java.Log;

@Log
@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardMapper boardMapper;

    @Autowired
    private BoardAttachMapper attachMapper;

    @Autowired
    private ReplyMapper replyMapper;

    @Transactional
    @Override
    public int register(BoardVO board) {
        log.info("register....." + board);

        int result = boardMapper.insert(board);
        if (board.getAttachList() == null || board.getAttachList().size() == 0) {
            return -1;
        }

        board.getAttachList().forEach(attach -> {
            attach.setBno(board.getBno());
            attachMapper.insert(attach);
        });

        return result;
    }

    @Override
    public BoardVO get(Long bno) {
        return boardMapper.select(bno);
    }

    @Transactional
    @Override
    public int remove(Long bno) {
        log.info("remove..." + bno);
        attachMapper.deleteAllByBno(bno);
        replyMapper.deleteAllByBno(bno);
        return boardMapper.delete(bno);
    }

    @Override
    public List<BoardVO> getList(Criteria cri) {
        return boardMapper.getListWithPaging(cri);
    }

    @Override
    public boolean modify(BoardVO board) {
        log.info("modify..." + board);

        attachMapper.deleteAllByBno(board.getBno());

        boolean modifyResult = boardMapper.update(board) == 1;

        if(modifyResult) {
            if(board.getAttachList() != null && board.getAttachList().size() > 0) {
                board.getAttachList().forEach(attach -> {
                    attach.setBno(board.getBno());
                    attachMapper.insert(attach);
                });
            }
        }
        return modifyResult;
    }

    @Override
    public int getTotol(Criteria cri) {
        log.info("get total count");
        return boardMapper.getTotalCount(cri);
    }

    @Override
    public int updateReplyCnt(Long bno) {
        return boardMapper.updateReplyCnt(bno);
    }

    @Override
    public List<BoardAttachVO> getAttachList(Long bno) {
        return attachMapper.findByBno(bno);
    }
}