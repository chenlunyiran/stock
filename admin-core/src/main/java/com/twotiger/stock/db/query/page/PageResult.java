package com.twotiger.stock.db.query.page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2016/1/1
 * Time: 20:21
 * To change this template use File | Settings | File Templates.
 */
public class PageResult<E>  implements IPageResult {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageResult.class);

    private static final long serialVersionUID = 2187283487105513474L;

    public static final PageResult DEFAULT_EMPTY = new PageResult(0,10,0,0,0,8, Collections.EMPTY_LIST);

    public static final PageResult EMPTY_OF_PAGESIZE(int pageSize){
        return new PageResult(0,pageSize,0,0,0,8, Collections.EMPTY_LIST );
    }

    public PageResult(){}

    /**
     *
     * @param pageNum 当前页
     * @param pageSize 每页的数量
     * @param pages 总页数
     * @param size 当前页的数量
     * @param total 总记录数
     * @param navigatePages 导航页码数
     * @param list
     */
    public PageResult(int pageNum,int pageSize,int pages,int size,long total,int navigatePages,List<E> list){
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = pages;
        this.list = list;
        this.size = size;
        this.total = total;
        calculateStartAndEndRow();
        //由于结果是>startRow的，所以实际的需要+1
        if (this.size == 0) {
            this.startRow = 0;
            this.endRow = 0;
        } else {
            this.startRow = this.startRow + 1;
            //计算实际的endRow（最后一页的时候特殊）
            this.endRow = this.startRow - 1 + this.size;
        }
        this.navigatePages = navigatePages;
        //计算导航页
        calcNavigatepageNums();
        //计算前后页，第一页，最后一页
        calcPage();
        //判断页面边界
        judgePageBoudary();
    }

    /**
     *
     * @param pageNum 当前页
     * @param pageSize 每页的数量
     * @param pages 总页数
     * @param size 当前页的数量
     * @param total 总记录数
     * @param navigatePages 导航页码数
     * @param list
     */
    public PageResult(int pageNum,int pageSize,int pages,int size,int total,int navigatePages,List<E> list){
        try {
            if (list != null && list.size() > pageSize) {
                ArrayList<E> res = new ArrayList<>();
                int begin = (pageNum - 1) * pageSize;
                int end = Integer.min(begin + pageSize, list.size());
                for (int i = begin; i < end; i++) {
                    res.add(list.get(i));
                }
                this.list = res;
            } else {
                this.list = list;
            }
        }catch (Exception e){
            LOGGER.error("PageResult error!",e);
            this.list = list;
        }

        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = pages;
        this.size = size;
        this.total = total;
        calculateStartAndEndRow();
        //由于结果是>startRow的，所以实际的需要+1
        if (this.size == 0) {
            this.startRow = 0;
            this.endRow = 0;
        } else {
            this.startRow = this.startRow + 1;
            //计算实际的endRow（最后一页的时候特殊）
            this.endRow = this.startRow - 1 + this.size;
        }
        this.navigatePages = navigatePages;
        //计算导航页
        calcNavigatepageNums();
        //计算前后页，第一页，最后一页
        calcPage();
        //判断页面边界
        judgePageBoudary();
    }

    /**
     *
     * @param pageNum 当前页
     * @param pageSize 每页的数量
     * @param total 总记录数
     * @param list
     */
    public PageResult(int pageNum,int pageSize,int total,List<E> list){
        this(pageNum,pageSize,(total+pageSize-1)/pageSize,list.size(),total,8,list);
    }

    /**
     * 第几页
     */
    private int pageNum = 1;
    /**
     * 每页记录大小
     */
    private int pageSize;
    /**
     * 当前页查询出的数量
     */
    private int size;
    /**
     * 开始记录行号
     */
    private int startRow;
    /**
     * 结束记录行号
     */
    private int endRow;
    /**
     * 总记录条数
     */
    private long total;
    /**
     * 总共页数
     */
    private int pages;
    /**
     * 数据
     */
    private List<E> list;
    /**
     * 第一页
     */
    private int firstPage;
    /**
     * 上一页
     */
    private int prePage;
    /**
     * 下一页
     */
    private int nextPage;
    /**
     * 最后一页
     */
    private int lastPage;
    /**
     * 是否是第一页
     */
    private boolean isFirstPage;
    /**
     * 是否是最后一页
     */
    private boolean isLastPage;
    /**
     * 是否能上翻
     */
    private boolean hasPreviousPage;
    /**
     * 是否能下翻
     */
    private boolean hasNextPage;

    /**
     * 导航页码数
     */
    private int navigatePages;

    /**
     * 所有导航页号
     */
    private int[] navigatepageNums;

//    public int currentPage;
//
//    public int totalPage;
//
//    public int numPerPage;

    /**
     * Getter for property 'endRow'.
     *
     * @return Value for property 'endRow'.
     */
    public int getEndRow() {
        return endRow;
    }

    /**
     * Setter for property 'endRow'.
     *
     * @param endRow Value to set for property 'endRow'.
     */
    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }

    /**
     * Getter for property 'firstPage'.
     *
     * @return Value for property 'firstPage'.
     */
    public int getFirstPage() {
        return firstPage;
    }

    /**
     * Setter for property 'firstPage'.
     *
     * @param firstPage Value to set for property 'firstPage'.
     */
    public void setFirstPage(int firstPage) {
        this.firstPage = firstPage;
    }

    /**
     * Getter for property 'hasNextPage'.
     *
     * @return Value for property 'hasNextPage'.
     */
    public boolean isHasNextPage() {
        return hasNextPage;
    }

    /**
     * Setter for property 'hasNextPage'.
     *
     * @param hasNextPage Value to set for property 'hasNextPage'.
     */
    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    /**
     * Getter for property 'hasPreviousPage'.
     *
     * @return Value for property 'hasPreviousPage'.
     */
    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    /**
     * Setter for property 'hasPreviousPage'.
     *
     * @param hasPreviousPage Value to set for property 'hasPreviousPage'.
     */
    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    /**
     * Getter for property 'firstPage'.
     *
     * @return Value for property 'firstPage'.
     */
    public boolean isFirstPage() {
        return isFirstPage;
    }

    /**
     * Setter for property 'isFirstPage'.
     *
     * @param isFirstPage Value to set for property 'isFirstPage'.
     */
    public void setIsFirstPage(boolean isFirstPage) {
        this.isFirstPage = isFirstPage;
    }

    /**
     * Getter for property 'lastPage'.
     *
     * @return Value for property 'lastPage'.
     */
    public boolean isLastPage() {
        return isLastPage;
    }

    /**
     * Setter for property 'isLastPage'.
     *
     * @param isLastPage Value to set for property 'isLastPage'.
     */
    public void setIsLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    /**
     * Getter for property 'lastPage'.
     *
     * @return Value for property 'lastPage'.
     */
    public int getLastPage() {
        return lastPage;
    }

    /**
     * Setter for property 'lastPage'.
     *
     * @param lastPage Value to set for property 'lastPage'.
     */
    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    /**
     * Getter for property 'list'.
     *
     * @return Value for property 'list'.
     */
    public List<E> getList() {
        return list;
    }

    /**
     * Setter for property 'list'.
     *
     * @param list Value to set for property 'list'.
     */
    public void setList(List<E> list) {
        this.list = list;
    }

    /**
     * Getter for property 'navigatepageNums'.
     *
     * @return Value for property 'navigatepageNums'.
     */
    public int[] getNavigatepageNums() {
        return navigatepageNums;
    }

    /**
     * Setter for property 'navigatepageNums'.
     *
     * @param navigatepageNums Value to set for property 'navigatepageNums'.
     */
    public void setNavigatepageNums(int[] navigatepageNums) {
        this.navigatepageNums = navigatepageNums;
    }

    /**
     * Getter for property 'navigatePages'.
     *
     * @return Value for property 'navigatePages'.
     */
    public int getNavigatePages() {
        return navigatePages;
    }

    /**
     * Setter for property 'navigatePages'.
     *
     * @param navigatePages Value to set for property 'navigatePages'.
     */
    public void setNavigatePages(int navigatePages) {
        this.navigatePages = navigatePages;
    }

    /**
     * Getter for property 'nextPage'.
     *
     * @return Value for property 'nextPage'.
     */
    public int getNextPage() {
        return nextPage;
    }

    /**
     * Setter for property 'nextPage'.
     *
     * @param nextPage Value to set for property 'nextPage'.
     */
    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    /**
     * Getter for property 'pageNum'.
     *
     * @return Value for property 'pageNum'.
     */
    public int getPageNum() {
        return pageNum;
    }

    /**
     * Setter for property 'pageNum'.
     *
     * @param pageNum Value to set for property 'pageNum'.
     */
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
        //this.currentPage=pageNum;
    }

    /**
     * Getter for property 'pages'.
     *
     * @return Value for property 'pages'.
     */
    public int getPages() {
        return pages;
    }

    /**
     * Setter for property 'pages'.
     *
     * @param pages Value to set for property 'pages'.
     */
    public void setPages(int pages) {
        this.pages = pages;
        //this.totalPage=pages;
    }

    /**
     * Getter for property 'pageSize'.
     *
     * @return Value for property 'pageSize'.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Setter for property 'pageSize'.
     *
     * @param pageSize Value to set for property 'pageSize'.
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        //this.numPerPage =pageSize;
    }

    /**
     * Getter for property 'prePage'.
     *
     * @return Value for property 'prePage'.
     */
    public int getPrePage() {
        return prePage;
    }

    /**
     * Setter for property 'prePage'.
     *
     * @param prePage Value to set for property 'prePage'.
     */
    public void setPrePage(int prePage) {
        this.prePage = prePage;
    }

    /**
     * Getter for property 'size'.
     *
     * @return Value for property 'size'.
     */
    public int getSize() {
        return size;
    }

    /**
     * Setter for property 'size'.
     *
     * @param size Value to set for property 'size'.
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Getter for property 'startRow'.
     *
     * @return Value for property 'startRow'.
     */
    public int getStartRow() {
        return startRow;
    }

    /**
     * Setter for property 'startRow'.
     *
     * @param startRow Value to set for property 'startRow'.
     */
    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    /**
     * Getter for property 'total'.
     *
     * @return Value for property 'total'.
     */
    public long getTotal() {
        return total;
    }

    /**
     * Setter for property 'total'.
     *
     * @param total Value to set for property 'total'.
     */
    public void setTotal(long total) {
        this.total = total;
    }


    /**
     * 计算起止行号
     */
    private void calculateStartAndEndRow() {
        this.startRow = this.pageNum > 0 ? (this.pageNum - 1) * this.pageSize : 0;
        this.endRow = this.startRow + this.pageSize * (this.pageNum > 0 ? 1 : 0);
    }

    /**
     * 计算导航页
     */
    private void calcNavigatepageNums() {
        //当总页数小于或等于导航页码数时
        if (pages <= navigatePages) {
            navigatepageNums = new int[pages];
            for (int i = 0; i < pages; i++) {
                navigatepageNums[i] = i + 1;
            }
        } else { //当总页数大于导航页码数时
            navigatepageNums = new int[navigatePages];
            int startNum = pageNum - navigatePages / 2;
            int endNum = pageNum + navigatePages / 2;

            if (startNum < 1) {
                startNum = 1;
                //(最前navigatePages页
                for (int i = 0; i < navigatePages; i++) {
                    navigatepageNums[i] = startNum++;
                }
            } else if (endNum > pages) {
                endNum = pages;
                //最后navigatePages页
                for (int i = navigatePages - 1; i >= 0; i--) {
                    navigatepageNums[i] = endNum--;
                }
            } else {
                //所有中间页
                for (int i = 0; i < navigatePages; i++) {
                    navigatepageNums[i] = startNum++;
                }
            }
        }
    }

    /**
     * 计算前后页，第一页，最后一页
     */
    private void calcPage() {
        if (navigatepageNums != null && navigatepageNums.length > 0) {
            firstPage = navigatepageNums[0];
            lastPage = navigatepageNums[navigatepageNums.length - 1];
            if (pageNum > 1) {
                prePage = pageNum - 1;
            }
            if (pageNum < pages) {
                nextPage = pageNum + 1;
            }
        }
    }

    /**
     * 判定页面边界
     */
    private void judgePageBoudary() {
        isFirstPage = pageNum == 1;
        isLastPage = pageNum == pages;
        hasPreviousPage = pageNum > 1;
        hasNextPage = pageNum < pages;
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", size=" + size +
                ", startRow=" + startRow +
                ", endRow=" + endRow +
                ", total=" + total +
                ", pages=" + pages +
                ", list=" + list +
                ", firstPage=" + firstPage +
                ", prePage=" + prePage +
                ", nextPage=" + nextPage +
                ", lastPage=" + lastPage +
                ", isFirstPage=" + isFirstPage +
                ", isLastPage=" + isLastPage +
                ", hasPreviousPage=" + hasPreviousPage +
                ", hasNextPage=" + hasNextPage +
                ", navigatePages=" + navigatePages +
                ", navigatepageNums=" + Arrays.toString(navigatepageNums) +
                '}';
    }
}
