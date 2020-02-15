package com.example.demo.service;

import com.example.demo.domain.Criteria;
import com.example.demo.domain.ReplyPageDTO;
import com.example.demo.domain.ReplyVO;

public interface ReplyService {
    public int register(ReplyVO reply);
    public ReplyVO get(Long rno);
    public int modify(ReplyVO reply);
    public int remove(Long rno);
    public ReplyPageDTO getListPage(Criteria cri, Long bno);
}