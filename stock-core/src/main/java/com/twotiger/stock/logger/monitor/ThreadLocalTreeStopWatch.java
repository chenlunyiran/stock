package com.twotiger.stock.logger.monitor;


import com.twotiger.stock.logger.util.TreeStopWatch;

/**
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2016/1/27
 * Time: 22:15
 * To change this template use File | Settings | File Templates.
 */
public final class ThreadLocalTreeStopWatch {
    private ThreadLocalTreeStopWatch() {
    }

    public static final ThreadLocal<TreeStopWatch> LOCAL_TREE_STOP_WATCH = new ThreadLocal<>();
}
