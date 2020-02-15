package com.example.demo.mapper;

import com.example.demo.domain.UploadPathVO;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UploadPathMapper {
	UploadPathVO select();
	void truncate();
	void insert(UploadPathVO uploadPathVO);
	UploadPathVO getYesterDayFolder();
}
