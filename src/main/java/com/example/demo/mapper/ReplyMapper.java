package com.example.demo.mapper;

import java.util.List;

import com.example.demo.domain.Criteria;
import com.example.demo.domain.ReplyVO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReplyMapper {
    public int insert(ReplyVO reply);
    public ReplyVO read(Long rno);
    public int update(ReplyVO reply);
    public int delete(Long rno);

    public List<ReplyVO> getListWithPaging(
        @Param("cri") Criteria cri,
        @Param("bno") Long bno
    );

    public int getCountByBno(Long bno);

    public int deleteAllByBno(Long bno);
}