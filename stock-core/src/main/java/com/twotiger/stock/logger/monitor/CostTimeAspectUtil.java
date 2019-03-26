package com.twotiger.stock.logger.monitor;


import com.twotiger.stock.logger.constant.ConstantLog;
import com.twotiger.stock.logger.util.SimpleInvokerInfoBuilder;
import com.twotiger.stock.logger.util.TreeStopWatch;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Created by liuqing-notebook on 2016/2/3.
 */
public class CostTimeAspectUtil {
    private final static Logger LOGGER = ConstantLog.MONITOR_COSTTIME_LOG;

    /**
     * <br>描 述：通用日志拦截处理流程（非入口）
     * <br>作 者：liuqing
     * <br>历 史: (版本) 作者 时间 注释
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    public static Object commonCall(ProceedingJoinPoint pjp) throws Throwable {
        Object resultObject = null;
        boolean isNew = false;
        if (ConstantLog.MONITOR_COSTTIME_LOG.isInfoEnabled()) {
            TreeStopWatch treeStopWatch = ThreadLocalTreeStopWatch.LOCAL_TREE_STOP_WATCH.get();
            if (treeStopWatch == null) {
                treeStopWatch = new TreeStopWatch(6);
                ThreadLocalTreeStopWatch.LOCAL_TREE_STOP_WATCH.set(treeStopWatch);
                isNew = true;
            }
            if (treeStopWatch != null) {
                try {
                    treeStopWatch.start(SimpleInvokerInfoBuilder.INSTANCE.buildFrom(pjp));
                    resultObject = pjp.proceed();
                    treeStopWatch.setResult(String.valueOf(resultObject));
                } catch (Exception e) {
                    treeStopWatch.setError(e);
                    throw e;
                } finally {
                    try {
                        treeStopWatch.stop();
                    } catch (Exception e) {
                        LOGGER.error("treeStopWatch.stop error", e);
                    }
                    if (isNew) {
                        try {
                            if (treeStopWatch.hasError()) {
                                LOGGER.error(treeStopWatch.formatResult(), treeStopWatch.getError());
                            } else {
                                LOGGER.info(treeStopWatch.formatResult());
                            }
                        } finally {
                            ThreadLocalTreeStopWatch.LOCAL_TREE_STOP_WATCH.remove();
                        }
                    }
                }

            } else {
                resultObject = pjp.proceed();
            }
        } else {
            resultObject = pjp.proceed();
        }
        return resultObject;
    }
}
