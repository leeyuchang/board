package com.example.demo.mapper;

import com.example.demo.domain.UploadPathVO;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UploadPathMapper {
	public UploadPathVO select();
	public void truncate();
	public void insert(UploadPathVO uploadPathVO);
	public UploadPathVO getYesterDayFolder();
}
