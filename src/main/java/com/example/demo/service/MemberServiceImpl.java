package com.example.demo.service;

import com.example.demo.domain.MemberVO;
import com.example.demo.mapper.MemberMapper;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.extern.java.Log;

@Log
@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private PasswordEncoder pwEncoder;

    @Autowired
    private MemberMapper memberMapper;

    @Override
    public MemberVO read(String userid) {
        log.info(String.join(":", this.getClass().getSimpleName(), userid));
        return memberMapper.read(userid);
    }

    @Override
    public boolean signUp(MemberVO member) {
        
        MemberVO exist = memberMapper.read(member.getUserid());
        if(exist != null) {
            return false;
        }
        
        String encryptPw = pwEncoder.encode(member.getUserpw());
        MemberVO newMember = new MemberVO();
        BeanUtils.copyProperties(member, newMember);
        newMember.setUserpw(encryptPw);
        return memberMapper.insert(newMember) == 1;
    }
    
}