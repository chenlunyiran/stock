package com.twotiger.stock.db.query.page;




import com.twotiger.stock.db.query.Duad;
import com.twotiger.stock.db.query.PageConfig;
import com.twotiger.stock.db.query.ex.MorePageSizeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页查询条件
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2016/1/1
 * Time: 19:58
 * To change this template use File | Settings | File Templates.
 */
public class PageParam implements IPage {


    private static final long serialVersionUID = 1130017323731968950L;

    /**
     *
     * @param pageNum 当前页数
     * @param pageSize 每页数据大小
     * @return
     */
    public static PageParam of(int pageNum, int pageSize){
        PageParam page = new PageParam();
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        return page;
    }
    /**
     * 页数
     */
    private int pageNum = 1;
    /**
     * 每页大小
     */
    private int pageSize = 10;

    /**
     * 自动计算总数
     */
    private boolean autoCount = true;

    /**
     * 导航页码数
     */
    private int navigatePages = 8;

    /**
     * 排序条件
     */
    private final List<Duad<String, String>> orders = new ArrayList<>();

    /**
     * Getter for property 'pageNum'.
     *
     * @return Value for property 'pageNum'.
     */
    public int getPageNum() {
        return pageNum;
    }

    @Override
    public int getPageSize() {
        return pageSize>0?pageSize:PageConfig.DEFAULT_PAGE_SIZE;
    }

    @Override
    public int getNavigatePages() {
        return navigatePages;
    }

    @Override
    public boolean isAutoCount() {
        return autoCount;
    }

    @Override
    public List<Duad<String, String>> getOrders() {
        return orders;
    }

    /**
     * Setter for property 'pageNum'.
     *
     * @param pageNum Value to set for property 'pageNum'.
     */
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    @Override
    public void setNavigatePages(int navigatePages) {
        this.navigatePages=navigatePages;
    }

    /**
     * Setter for property 'pageSize'.
     *
     * @param pageSize Value to set for property 'pageSize'.
     */
    public final void setPageSize(int pageSize) {
        if(pageSize> PageConfig.MAX_PAGE_SIZE){
            throw new MorePageSizeException("query pageSize="+pageSize+" but MAX_PAGE_SIZE="+ PageConfig.MAX_PAGE_SIZE);
        }
        this.pageSize = pageSize;
    }

    @Override
    public void setAutoCount(boolean autoCount) {
        this.autoCount = autoCount;
    }

    /**
     * 兼容 curPage 参数
     * @return
     */
    public int getCurPage() {
        return getPageNum();
    }

    /**
     * 兼容 curPage 参数
     */
    public void setCurPage(int curPage) {
        setPageNum(curPage);
    }

    @Override
    public String toString() {
        return "PageParam{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", autoCount=" + autoCount +
                ", navigatePages=" + navigatePages +
                ", orders=" + orders +
                '}';
    }



    /////////////////////////// use/////////////////////////////
    /**
     * params a map which contains the query parameters
     */
    private Map<String, Object> params  = new HashMap<String, Object>();

   //当前页
    public int getCurrentPage() {
        return this.getPageNum();
    }
    //当前页
    public void setCurrentPage(int currentPage) {
        this.setPageNum(currentPage);
    }
    //当前页
    public int getOffset(){return this.getPageNum();}
    //当前页
    public void setOffset(int offset){this.setPageNum(offset);}

    /** 每页记录数 */
    public int getNumPerPage() {
        return getPageSize();
    }

    /** 每页记录数 */
    public void setNumPerPage(int numPerPage) {
        setPageSize(numPerPage);
    }

    /** 每页记录数 */
    public int getLimit(){return this.getPageSize();}
    /** 每页记录数 */
    public void setLimit(int limit){this.setPageSize(limit);}

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    /**
     * 计算总页数 .
     *
     * @param totalCount
     *            总记录数.
     * @param numPerPage
     *            每页记录数.
     * @return totalPage 总页数.
     */
    public static int countTotalPage(int totalCount, int numPerPage) {
        if (totalCount % numPerPage == 0) {
            // 刚好整除
            return totalCount / numPerPage;
        } else {
            // 不能整除则总页数为：商 + 1
            return totalCount / numPerPage + 1;
        }
    }

    /**
     * 校验当前页数currentPage.<br/>
     * 1、先根据总记录数totalCount和每页记录数numPerPage，计算出总页数totalPage.<br/>
     * 2、判断页面提交过来的当前页数currentPage是否大于总页数totalPage，大于则返回totalPage.<br/>
     * 3、判断currentPage是否小于1，小于则返回1.<br/>
     * 4、其它则直接返回currentPage .
     *
     * @param totalCount
     *            要分页的总记录数 .
     * @param numPerPage
     *            每页记录数大小 .
     * @param currentPage
     *            输入的当前页数 .
     * @return currentPage .
     */
    public static int checkCurrentPage(int totalCount, int numPerPage,
                                       int currentPage) {
        int totalPage = countTotalPage(totalCount, numPerPage); // 最大页数
        if (currentPage > totalPage) {
            // 如果页面提交过来的页数大于总页数，则将当前页设为总页数
            // 此时要求totalPage要大于获等于1
            if (totalPage < 1) {
                return 1;
            }
            return totalPage;
        } else if (currentPage < 1) {
            return 1; // 当前页不能小于1（避免页面输入不正确值）
        } else {
            return currentPage;
        }
    }

    /**
     * 校验页面输入的每页记录数numPerPage是否合法 .<br/>
     * 1、当页面输入的每页记录数numPerPage大于允许的最大每页记录数MAX_PAGE_SIZE时，返回MAX_PAGE_SIZE.
     * 2、如果numPerPage小于1，则返回默认的每页记录数DEFAULT_PAGE_SIZE.
     *
     * @param numPerPage
     *            页面输入的每页记录数 .
     * @return checkNumPerPage .
     */
    public static int checkNumPerPage(int numPerPage) {
        if (numPerPage > PageConfig.MAX_PAGE_SIZE) {
            return PageConfig.MAX_PAGE_SIZE;
        } else if (numPerPage < 1) {
            return PageConfig.DEFAULT_PAGE_SIZE;
        } else {
            return numPerPage;
        }
    }

}
