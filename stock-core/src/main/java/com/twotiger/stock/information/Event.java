package com.twotiger.stock.information;


/**
 * 事件
 * Created by liuqing on 2015/4/1.
 */
public interface Event<D> extends Information{
    /**
     * 获得事件数据
     * @return
     */
    D data();
}
