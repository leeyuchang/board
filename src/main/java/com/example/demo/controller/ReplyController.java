package com.example.demo.controller;

import com.example.demo.domain.Criteria;
import com.example.demo.domain.ReplyPageDTO;
import com.example.demo.domain.ReplyVO;
import com.example.demo.service.ReplyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.java.Log;

@Log
@RequestMapping("/replies")
@Controller
public class ReplyController {

    @Autowired
    private ReplyService replyService;

    @PostMapping("/new")
    public ResponseEntity<String> create(@RequestBody ReplyVO reply) {
        log.info("/new :" + reply);
        int insertCount = replyService.register(reply);
        log.info("Reply INSERT COUNT : " + insertCount);
        return insertCount == 1 ? new ResponseEntity<>("success", HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/pages/{bno}/{page}")
    public ResponseEntity<ReplyPageDTO> getList(@PathVariable("page") int page, @PathVariable("bno") Long bno) {
        Criteria cri = new Criteria(page, 5);
        log.info("get Reply List bno: " + bno);
        log.info("cri:" + cri);
        return new ResponseEntity<>(replyService.getListPage(cri, bno), HttpStatus.OK);
    }

    @GetMapping("/{rno}")
    public ResponseEntity<ReplyVO> get(@PathVariable("rno") Long rno) {
        log.info("/{rno}:" + rno);
        ReplyVO reply = replyService.get(rno);
        return new ResponseEntity<>(reply, HttpStatus.OK);
    }

    @PutMapping("/{rno}")
    public ResponseEntity<String> modify(@RequestBody ReplyVO reply, @PathVariable("rno") Long rno) {
        log.info("★/{rno} : " + rno);
        log.info("★ReplyVO : " + reply);

        reply.setRno(rno);
        return replyService.modify(reply) == 1 ? new ResponseEntity<>("success", HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/{rno}")
    public ResponseEntity<String> remove(@PathVariable("rno") Long rno) {
        log.info("@DeleteMapping");
        return replyService.remove(rno) == 1 ? new ResponseEntity<>("success", HttpStatus.OK) : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}