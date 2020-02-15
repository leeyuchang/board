package com.example.demo.mapper;

import java.util.List;

import com.example.demo.domain.BoardVO;
import com.example.demo.domain.Criteria;

import org.apache.ibatis.annotations.Mapper;

/**
 * BoardMapper
 */
@Mapper
public interface BoardMapper{
    public List<BoardVO> getList();
    public List<BoardVO> getListWithPaging(Criteria cri);
    public int insert(BoardVO board);
    public BoardVO select(Long bno);
    public int delete(Long bno);
    public int update(BoardVO board);
    public int getTotalCount(Criteria cri);
    public int updateReplyCnt(Long bno);
}