package com.twotiger.stock.db.query.page;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询结果
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2016/1/1
 * Time: 16:59
 * To change this template use File | Settings | File Templates.
 */
public interface IPageResult<E> extends Serializable{
    /**
     * 结束记录行号.
     *
     * @return Value for property 'endRow'.
     */
     int getEndRow();

    /**
     * 第一页.
     *
     * @return Value for property 'firstPage'.
     */
     int getFirstPage();

    /**
     * 是否能下翻.
     *
     * @return Value for property 'hasNextPage'.
     */
     boolean isHasNextPage() ;

    /**
     * 是否能上翻.
     *
     * @return Value for property 'hasPreviousPage'.
     */
     boolean isHasPreviousPage();


    /**
     * 是否是第一页.
     *
     * @return Value for property 'firstPage'.
     */
     boolean isFirstPage();


    /**
     * 是否是最后一页.
     *
     * @return Value for property 'lastPage'.
     */
     boolean isLastPage();

    /**
     * 最后一页.
     *
     * @return Value for property 'lastPage'.
     */
     int getLastPage();


    /**
     * 数据.
     *
     * @return Value for property 'list'.
     */
     List<E> getList();



    /**
     * 所有导航页号.
     *
     * @return Value for property 'navigatepageNums'.
     */
     int[] getNavigatepageNums();


    /**
     * 导航页码数.
     *
     * @return Value for property 'navigatePages'.
     */
     int getNavigatePages();


    /**
     * 下一页.
     *
     * @return Value for property 'nextPage'.
     */
     int getNextPage() ;


    /**
     * 第几页.
     *
     * @return Value for property 'pageNum'.
     */
     int getPageNum();


    /**
     * 总共页数.
     *
     * @return Value for property 'pages'.
     */
     int getPages();


    /**
     * 每页记录大小.
     *
     * @return Value for property 'pageSize'.
     */
     int getPageSize();


    /**
     * 上一页.
     *
     * @return Value for property 'prePage'.
     */
     int getPrePage();

    /**
     * 当前页查询出的数量.
     *
     * @return Value for property 'size'.
     */
     int getSize();

    /**
     * 开始记录行号.
     *
     * @return Value for property 'startRow'.
     */
     int getStartRow();

    /**
     * 总记录条数.
     *
     * @return Value for property 'total'.
     */
     long getTotal();

    /**
     * 本页的数据列表
     * @return
     */
     default List<E> getRecordList(){
        return getList();
     }

    /**
     * 当前页
     * @return
     */
    default int getCurrentPage(){
         return getPageNum();
     }

    /**
     * 总页数
     * @return
     */
    default int getTotalPage(){
        return getPages();
    }

    /**
     * 当前页
     * @return
     */
    default int getNumPerPage(){
        return getPageSize();
    }

    /**
     * 总记录数
     * @return
     */
    default int getTotalCount(){
        return (int)getTotal();
    }

    /**
     * 页码列表的开始索引
     * @return
     */
    default int getBeginPageIndex(){
        return getFirstPage();
    }

    /**
     * 页码列表的结束索引
     * @return
     */
    default int getEndPageIndex(){
        return getLastPage();
    }

}
