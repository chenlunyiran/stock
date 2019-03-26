package com.twotiger.stock.logger;

import com.twotiger.stock.logger.constant.ConstantLog;
import com.twotiger.stock.logger.util.InvokerInfo;
import com.twotiger.stock.logger.util.InvokerInfoBuilder;
import com.twotiger.stock.logger.util.ParamFormatter;
import com.twotiger.stock.logger.util.ResultFormatter;
import com.twotiger.stock.logger.util.SimpleInvokerInfoBuilder;
import com.twotiger.stock.logger.util.SimpleParamFormatter;
import com.twotiger.stock.logger.util.SimpleResultFormatter;
import com.twotiger.stock.logger.util.TreeStopWatch;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.aspectj.lang.ProceedingJoinPoint;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.twotiger.stock.logger.util.TreeStopWatch.DEFAULT_LEVEL_COUNT;
import static com.twotiger.stock.logger.util.TreeStopWatch.MAX_LEVEL_COUNT;

public final class TREE_LOG {

    private static final ThreadLocal<TreeStopWatch> LOCAL_TREE_STOP_WATCH = new ThreadLocal<TreeStopWatch>() {
        @Override
        protected TreeStopWatch initialValue() {
            return new TreeStopWatch(DEFAULT_LEVEL_COUNT);
        }
    };

    private static final InvokerInfoBuilder invokerInfoBuilder = SimpleInvokerInfoBuilder.INSTANCE;

    private static final ParamFormatter paramFormatter = new SimpleParamFormatter();

    private static final ResultFormatter resultFormatter = new SimpleResultFormatter();



    public static void begin() {
        if (ConstantLog.MONITOR_COSTTIME_LOG.isInfoEnabled()) {
            try {
                TreeStopWatch treeStopWatch = LOCAL_TREE_STOP_WATCH.get();
                InvokerInfo invokerInfo = new InvokerInfo("invokerToken", "treeLog", StackLocatorUtil.getCallerClass(2), null, null);
                treeStopWatch.start(invokerInfo);
            } catch (Exception e) {
                ConstantLog.MONITOR_COSTTIME_LOG.error("TREE_LOG begin error!", e);
            }
        }
    }

    public static void begin(int levelCount, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) {
        if (ConstantLog.MONITOR_COSTTIME_LOG.isInfoEnabled()) {
            try {
                TreeStopWatch treeStopWatch;
                if (levelCount > MAX_LEVEL_COUNT || levelCount < 1) {
                    treeStopWatch = LOCAL_TREE_STOP_WATCH.get();
                } else {
                    treeStopWatch = new TreeStopWatch(levelCount);
                    LOCAL_TREE_STOP_WATCH.set(treeStopWatch);
                }
                InvokerInfo invokerInfo = invokerInfoBuilder.buildFrom(httpServletRequest, o);
                treeStopWatch.start(invokerInfo);
            } catch (Exception e) {
                ConstantLog.MONITOR_COSTTIME_LOG.error("TREE_LOG begin error!", e);
            }
        }
    }

    public static void begin(int levelCount, ServletRequest request, ServletResponse response, FilterChain chain) {
        if (ConstantLog.MONITOR_COSTTIME_LOG.isInfoEnabled()) {
            try {
                TreeStopWatch treeStopWatch;
                if (levelCount > MAX_LEVEL_COUNT || levelCount < 1) {
                    treeStopWatch = LOCAL_TREE_STOP_WATCH.get();
                } else {
                    treeStopWatch = new TreeStopWatch(levelCount);
                    LOCAL_TREE_STOP_WATCH.set(treeStopWatch);
                }
                InvokerInfo invokerInfo = invokerInfoBuilder.buildFrom(request);
                treeStopWatch.start(invokerInfo);
            } catch (Exception e) {
                ConstantLog.MONITOR_COSTTIME_LOG.error("TREE_LOG.begin error!", e);
            }
        }
    }

    public static void end() {
        if (ConstantLog.MONITOR_COSTTIME_LOG.isInfoEnabled()) {
            try {
                TreeStopWatch treeStopWatch = LOCAL_TREE_STOP_WATCH.get();
                treeStopWatch.stop();
                if (treeStopWatch.hasError()) {
                    ConstantLog.MONITOR_COSTTIME_LOG.error(treeStopWatch.formatResult(), treeStopWatch.getError());
                } else {
                    ConstantLog.MONITOR_COSTTIME_LOG.info(treeStopWatch.formatResult());
                }
            } catch (Exception e) {
                ConstantLog.MONITOR_COSTTIME_LOG.error("TREE_LOG.end error!", e);
            } finally {
                LOCAL_TREE_STOP_WATCH.remove();
            }
        }
    }

    public static Object aroundCall(ProceedingJoinPoint pjp) throws Throwable {
        Object resultObject = null;
        if (ConstantLog.MONITOR_COSTTIME_LOG.isInfoEnabled()) {
            TreeStopWatch treeStopWatch = LOCAL_TREE_STOP_WATCH.get();
            try {
                try {
                    treeStopWatch.start(invokerInfoBuilder.buildFrom(pjp));
                } catch (Exception e) {
                    ConstantLog.MONITOR_COSTTIME_LOG.error("TREE_LOG.aroundCall treeStopWatch.start error", e);
                }
                resultObject = pjp.proceed();
                try {
                    treeStopWatch.setResult(resultFormatter.format(resultObject));
                } catch (Exception e) {
                    ConstantLog.MONITOR_COSTTIME_LOG.error("TREE_LOG.aroundCall treeStopWatch.setResult error", e);
                }
            } catch (Exception e) {
                treeStopWatch.setError(e);
                throw e;
            } finally {
                try {
                    treeStopWatch.stop();
                } catch (Exception e) {
                    ConstantLog.MONITOR_COSTTIME_LOG.error("TREE_LOG.aroundCall treeStopWatch.stop error", e);
                }
            }
        } else {
            resultObject = pjp.proceed();
        }
        return resultObject;
    }
}
