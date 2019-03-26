package com.twotiger.stock.db.mybatis;

import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.Marker;
import tk.mybatis.mapper.common.MySqlMapper;
import tk.mybatis.mapper.common.RowBoundsMapper;

/**
 * {@link tk.mybatis.mapper.common.Mapper}
 * Created by liuqing-notebook on 2017/2/6.
 */
public interface NoExampleMapper<T> extends BaseMapper<T>, RowBoundsMapper<T>,MySqlMapper<T>, Marker {
}
