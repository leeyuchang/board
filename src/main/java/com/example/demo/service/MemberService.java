package com.example.demo.service;

import com.example.demo.domain.MemberVO;

public interface MemberService {
    public MemberVO read(String userid);
    public boolean signUp(MemberVO member);
}