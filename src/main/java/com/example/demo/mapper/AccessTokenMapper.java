package com.example.demo.mapper;

import com.example.demo.domain.AccessTokenDTO;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccessTokenMapper {
	public int insert(AccessTokenDTO token);
	public AccessTokenDTO select();
	public int truncate();
}
