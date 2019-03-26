package com.twotiger.stock.logger.monitor;


import com.twotiger.stock.logger.constant.ConstantLog;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;


/**
 * <br>类 名: CostTimeAspect
 * <br>描 述: 调用计时日志切面：记录调用的顺序
 * <br>作 者: liuqing
 * <br>创 建： 2015年6月29日
 * <br>版 本：1.5.2
 *
 * <br>历 史: (版本) 作者 时间 注释
 */
@Aspect
public class CostTimeAspect {
    private final static Logger LOGGER = ConstantLog.MONITOR_COSTTIME_LOG;

    /**
     * <br>描 述：计时日志切面 服务层
     * <br>作 者：liuqing
     * <br>历 史: (版本) 作者 时间 注释
     *
     * @return
     */
    @Around(value = "execution( * com.service.impl.*.*(..))")
    public Object aroundCallService(ProceedingJoinPoint pjp) throws Throwable {
        return CostTimeAspectUtil.commonCall(pjp);
    }

    /**
     * <br>描 述：计时日志切面 DAO层
     * <br>作 者：liuqing
     * <br>历 史: (版本) 作者 时间 注释
     *
     * @return
     */
    @Around(value = "execution( * com.dao.imp.*.*(..))")
    public Object aroundCallDao(ProceedingJoinPoint pjp) throws Throwable {
        return CostTimeAspectUtil.commonCall(pjp);
    }


}
