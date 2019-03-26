package com.twotiger.stock.logger.monitor;

import com.twotiger.stock.logger.constant.ConstantLog;
import com.twotiger.stock.logger.util.SimpleInvokerInfoBuilder;
import com.twotiger.stock.logger.util.TreeStopWatch;
import org.apache.logging.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * 类 名: CostTimeFilter<br/>
 * 描 述: 性能耗时过滤器<br/>
 * 作 者: liuqing<br/>
 * 创 建： 2015-3-10<br/>
 * 版 本：1.5.0<br/>
 * <p>
 * 历 史: (1.5.2) liuqing 20150624 释放TREE_STOP_WATCH资源，日志中添加uuid<br/>
 */
public class CostTimeFilter implements Filter {
    private final static Logger LOGGER = ConstantLog.MONITOR_COSTTIME_LOG;

    private int levelCount = 5;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        if (LOGGER.isInfoEnabled()) {
            TreeStopWatch tsw = new TreeStopWatch(levelCount);
            try {
                ThreadLocalTreeStopWatch.LOCAL_TREE_STOP_WATCH.set(tsw);
                tsw.start(SimpleInvokerInfoBuilder.INSTANCE.buildFrom(request));
                try {
                    chain.doFilter(request, response);
                } finally {
                    tsw.stop();
                    LOGGER.info(tsw.formatResult());
                }
            } finally {
                ThreadLocalTreeStopWatch.LOCAL_TREE_STOP_WATCH.remove();
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String slevelCount = filterConfig.getInitParameter("levelCount");
        if (slevelCount != null) {
            this.levelCount = Integer.parseInt(slevelCount);
        }
    }
}
