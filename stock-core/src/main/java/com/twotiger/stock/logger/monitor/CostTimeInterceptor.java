package com.twotiger.stock.logger.monitor;


import com.twotiger.stock.logger.constant.ConstantLog;
import com.twotiger.stock.logger.util.SimpleInvokerInfoBuilder;
import com.twotiger.stock.logger.util.TreeStopWatch;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * springconfig 计时 拦截器
 * Created by liuqing-notebook on 2016/1/28.
 */
public class CostTimeInterceptor implements HandlerInterceptor {
    private final static Logger LOGGER = ConstantLog.MONITOR_COSTTIME_LOG;

    /**
     * TreeStopWatch统计深度
     */
    private int levelCount = 5;

    private boolean monitorLevel = true;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        if (LOGGER.isInfoEnabled()) {
            TreeStopWatch tsw = new TreeStopWatch(levelCount);
            tsw.setMonitorLevel(monitorLevel);
            ThreadLocalTreeStopWatch.LOCAL_TREE_STOP_WATCH.set(tsw);
            tsw.start(SimpleInvokerInfoBuilder.INSTANCE.buildFrom(httpServletRequest));
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        if (LOGGER.isInfoEnabled()) {
            try {
                TreeStopWatch tsw = ThreadLocalTreeStopWatch.LOCAL_TREE_STOP_WATCH.get();
                if (tsw.hasError()) {
                    tsw.stop();
                    LOGGER.error(tsw.formatResult(), tsw.getError());
                } else if (e != null) {
                    tsw.setError(e);
                    tsw.stop();
                    LOGGER.error(tsw.formatResult(), e);
                } else {
                    tsw.stop();
                    LOGGER.info(tsw.formatResult());
                }
            } finally {
                ThreadLocalTreeStopWatch.LOCAL_TREE_STOP_WATCH.remove();
            }
        }
    }

    public int getLevelCount() {
        return levelCount;
    }

    public void setLevelCount(int levelCount) {
        this.levelCount = levelCount;
    }

    public boolean isMonitorLevel() {
        return monitorLevel;
    }

    public void setMonitorLevel(boolean monitorLevel) {
        this.monitorLevel = monitorLevel;
    }
}
