package com.twotiger.stock.db.query.page;

/**
 * 分页查询参数
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2016/1/1
 * Time: 17:01
 * To change this template use File | Settings | File Templates.
 */
public interface IPage extends IPageR {


    /**
     * 设置页数
     * @param pageNum
     */
    void setPageNum(int pageNum);

    /**
     * 设置导航页码数
     * @param navigatePages
     */
    void setNavigatePages(int navigatePages);

    /**
     * 设置每页大小
     * @param pageSize
     */
    void setPageSize(int pageSize);

    /**
     * 设置是否计算总数
     * @param autoCount
     */
    void setAutoCount(boolean autoCount);

}
