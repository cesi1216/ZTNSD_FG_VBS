package com.spider.utils;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * DataTablePageUtil
 * </p>
 *
 * @author msi-
 * @package: com.spider.utils
 * @description: DataTable插件格式通用返回实体类
 * @date: Created in 2020-09-27 15:17
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: msi-
 */
public class DataTablePageUtil<T> implements Serializable {

    private int nowPage;//当前页（当前页和页大小也传可不传，但下面4个必传！）
    private int pageSize;//页大小
    private int draw;//记录数
    private long recordsTotal;//总数量
    private long recordsFiltered;//总数据
    private List<T> data;//要展示的数据 bean

    public int getNowPage() {
        return nowPage;
    }

    public void setNowPage(int nowPage) {
        this.nowPage = nowPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}