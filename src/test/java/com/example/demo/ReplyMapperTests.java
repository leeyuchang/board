package com.example.demo;

import java.util.List;

import com.example.demo.domain.Criteria;
import com.example.demo.domain.ReplyVO;
import com.example.demo.mapper.ReplyMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.java.Log;

@Log
@SpringBootTest
public class ReplyMapperTests {

    @Autowired
    ReplyMapper mapper;
    
    @Test
    public void testCreate() {
        log.info(mapper.toString());
    }

    @Test
    public void testList2() {
        Criteria cri = new Criteria(1, 10);
        List<ReplyVO> replies = mapper.getListWithPaging(cri, 98245L);
        replies.forEach(reply -> log.info(reply + ""));
    }
}