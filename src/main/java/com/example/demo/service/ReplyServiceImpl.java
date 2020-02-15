package com.example.demo.service;

import com.example.demo.domain.Criteria;
import com.example.demo.domain.ReplyPageDTO;
import com.example.demo.domain.ReplyVO;
import com.example.demo.mapper.BoardMapper;
import com.example.demo.mapper.ReplyMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.java.Log;

@Log
@Service
public class ReplyServiceImpl implements ReplyService {

    @Autowired
    private ReplyMapper replyMapper;

    @Autowired
    private BoardMapper boardMapper;

    @Override
    public int register(ReplyVO reply) {
        log.info(this + ":" + reply);
        Long bno = reply.getBno();
        int result = replyMapper.insert(reply);
        boardMapper.updateReplyCnt(bno);
        return result;
    }

    @Override
    public ReplyVO get(Long rno) {
        log.info(this + ":" + rno);
        return replyMapper.read(rno);
    }

    @Override
    public int modify(ReplyVO reply) {
        log.info(this + ":" + reply);
        return replyMapper.update(reply);
    }

    @Override
    public int remove(Long rno) {
        log.info("★" + this.getClass().getSimpleName() + ":" + rno);
        ReplyVO oldReply = replyMapper.read(rno);
        int result = replyMapper.delete(rno);
        ReplyVO reply = replyMapper.read(rno);
        Long bno = (null == reply) ? oldReply.getBno() : reply.getBno();
        boardMapper.updateReplyCnt(bno);
        return result;
    }

    @Override
    public ReplyPageDTO getListPage(Criteria cri, Long bno) {
        log.info(this + ":" + cri + ":" + bno);

        return new ReplyPageDTO(replyMapper.getCountByBno(bno), replyMapper.getListWithPaging(cri, bno));
    }

}