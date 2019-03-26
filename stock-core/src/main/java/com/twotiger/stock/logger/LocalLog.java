package com.twotiger.stock.logger;


import com.twotiger.stock.logger.monitor.ThreadLocalTreeStopWatch;
import com.twotiger.stock.logger.util.TreeStopWatch;

/**
 * 本地日志
 * Created by liuqing-notebook on 2017/2/14.
 */
public final class LocalLog {

    /**
     * 本地日志是否开启
     *
     * @return
     */
    public static boolean isEnabled() {
        return ThreadLocalTreeStopWatch.LOCAL_TREE_STOP_WATCH.get() != null;
    }

    public static void localMessage(String msg) {
        TreeStopWatch treeStopWatch = ThreadLocalTreeStopWatch.LOCAL_TREE_STOP_WATCH.get();
        if (treeStopWatch != null) {
            treeStopWatch.addLog(msg);
        }
    }

    public static void localMessage(String format, Object arg) {
        TreeStopWatch treeStopWatch = ThreadLocalTreeStopWatch.LOCAL_TREE_STOP_WATCH.get();
        if (treeStopWatch != null) {
            treeStopWatch.addLog(String.format(format, arg));
        }
    }

    public static void localMessage(String format, Object arg1, Object arg2) {
        TreeStopWatch treeStopWatch = ThreadLocalTreeStopWatch.LOCAL_TREE_STOP_WATCH.get();
        if (treeStopWatch != null) {
            treeStopWatch.addLog(String.format(format, arg1, arg2));
        }
    }

    public static void localMessage(String format, Object... arguments) {
        TreeStopWatch treeStopWatch = ThreadLocalTreeStopWatch.LOCAL_TREE_STOP_WATCH.get();
        if (treeStopWatch != null) {
            treeStopWatch.addLog(String.format(format, arguments));
        }
    }
}
