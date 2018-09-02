package com.pyg.utils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by on 2018/8/6.
 */
public class PageResult implements Serializable{

    public PageResult(Long total, List<?> rows) {
        this.total = total;
        this.rows = rows;
    }

    private Long total;
    private List<?> rows;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }
}
