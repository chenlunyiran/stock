package com.twotiger.stock.core.pojo;

import java.util.List;

/**
 * Created by alean on 2018/11/19.
 */
public class PagePOJO<T,J> {
    private Integer total;
    private Integer currentPage;
    private Integer pageSize;
    private List<J> data;
    private T t;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<J> getData() {
        return data;
    }

    public void setData(List<J> data) {
        this.data = data;
    }
}
