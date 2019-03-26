package com.twotiger.stock.db.mybatis.page;

import com.github.pagehelper.PageInfo;
import com.twotiger.stock.db.query.page.PageResult;

/**
 * Created by liuqing-notebook on 2016/2/25.
 */
public class MybatisPageResultAdapter<E> extends PageResult<E> {
    public MybatisPageResultAdapter(PageInfo pageInfo){
        super(pageInfo.getPageNum(),pageInfo.getPageSize(),pageInfo.getPages(),pageInfo.getSize(),pageInfo.getTotal(),pageInfo.getNavigatePages(),pageInfo.getList());
    }
}
