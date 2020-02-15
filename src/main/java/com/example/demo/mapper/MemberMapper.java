package com.example.demo.mapper;

import com.example.demo.domain.MemberVO;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

    public MemberVO read(String userid);
    public int insert(MemberVO member);
    
}