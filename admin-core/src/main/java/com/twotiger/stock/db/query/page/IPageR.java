package com.twotiger.stock.db.query.page;

import com.twotiger.stock.db.query.Duad;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询参数 只读（供dao层使用）
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2016/1/1
 * Time: 20:25
 * To change this template use File | Settings | File Templates.
 */
public interface IPageR extends Serializable{
    /**
     * 取得页数
     * @return
     */
    int getPageNum();

    /**
     * 取得每页大小
     * @return
     */
    int getPageSize();

    /**
     * 导航页码数
     * @return
     */
    int getNavigatePages();

    /**
     * 获得查询对象时是否先自动执行count查询获取总记录数
     * @return
     */
    boolean isAutoCount();

    /**
     * 取得排序属性
     * 结构：
     * key1 desc
     * key2 asc
     * @return
     */
    List<Duad<String,String>> getOrders();
}
