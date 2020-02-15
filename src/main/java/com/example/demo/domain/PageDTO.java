package com.example.demo.domain;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PageDTO {

    private int endPage;
    private int startPage;
    private boolean prev;
    private boolean next;

    private Criteria cri;
    private int total;

    public PageDTO(Criteria cri, int total) {

        this.cri = cri;
        this.total = total;

        this.endPage = (int) (Math.ceil(cri.getPageNum() / 5.0)) * 5;
        this.startPage = this.endPage - (5 - 1);

        int realEnd = (int) (Math.ceil((total * 1.0) / cri.getAmount()));
        if (realEnd < this.endPage) {
            this.endPage = realEnd;
        }

        this.prev = this.startPage > 1;
        this.next = this.endPage < realEnd;
    }

}