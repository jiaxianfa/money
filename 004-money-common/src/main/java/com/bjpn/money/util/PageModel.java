package com.bjpn.money.util;

import java.io.Serializable;

public class PageModel implements Serializable {
    //首页
    private Integer firstPage = 1;
    //尾页    需要计算
    private Long lastPage;
    //当前页
    private Long cunPage;
    //每页显示条数    如果没有传参，默认是10条
    private Integer pageSize = 10;
    //总条数
    private Long totalCount;

    public PageModel() {
    }

    public PageModel(Integer firstPage, Long lastPage, Long cunPage, Integer pageSize, Long totalCount) {
        this.firstPage = firstPage;
        this.lastPage = lastPage;
        this.cunPage = cunPage;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
    }

    public PageModel(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(Integer firstPage) {
        this.firstPage = firstPage;
    }

    public Long getLastPage() {
        return totalCount%pageSize==0?totalCount/pageSize:totalCount/pageSize+1;
    }
    //尾页是需要计算的，不需要赋值
    /*public void setLastPage(Long lastPage) {
        this.lastPage = lastPage;
    }*/

    public Long getCunPage() {
        return cunPage;
    }

    public void setCunPage(Long cunPage) {
        this.cunPage = cunPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
