package com.example.demo.domain;

import org.springframework.web.util.UriComponentsBuilder;

import lombok.Data;

@Data
public class Criteria {

    private int pageNum;
    private int amount;

    private String type;
    private String keyword;

    public Criteria() {
        this(1, 10);
    }

    public Criteria(int pageNum, int amount) {
        this.pageNum = pageNum;
        this.amount = amount;
    }

    public String[] getTypeArr() {
        return type == null ? new String[] {} : type.split("");
    }

    public String getListLink() {
        UriComponentsBuilder param = UriComponentsBuilder.fromPath("")
        .queryParam("pageNum", getPageNum())
        .queryParam("amount", getAmount())
        .queryParam("type", getType())
        .queryParam("keyword", getKeyword());
        return param.toUriString();
        }
}