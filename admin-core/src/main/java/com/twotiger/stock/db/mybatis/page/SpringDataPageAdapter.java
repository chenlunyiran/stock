package com.twotiger.stock.db.mybatis.page;


import com.twotiger.stock.db.query.page.IPageR;
import com.twotiger.stock.db.query.page.PageResult;

/**
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2016/1/16
 * Time: 14:15
 * To change this template use File | Settings | File Templates.
 */
public class SpringDataPageAdapter<E> extends PageResult<E> {
    public SpringDataPageAdapter(org.springframework.data.domain.Page<E> page,IPageR pageQuery){
        super(pageQuery.getPageNum(), pageQuery.getPageSize(), page.getTotalPages(), page.getNumberOfElements(), page.getTotalElements(), pageQuery.getNavigatePages(), page.getContent());
    }
}
