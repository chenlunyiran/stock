package com.twotiger.stock.logger.util;

import java.util.LinkedList;

/**
 * 任务信息
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2016/10/30
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 */
public class TaskInfo {
    //层级
    final int level;
    //本层位置
    final int levelIndex;
    //上一层的位置
    final int supLevelIndex;
    //任务名称
    final String taskName;
    //任务开始时间
    final long beginTime;
    //任务结束时间
    long endTime;
    //任务耗时
    long costTime;
    //异常信息
    String errorMessage = "无异常";

    //方法执行结果
    Object result = "";

    //层级日志
    LinkedList<String> logs = null;

    //调用信息
    final InvokerInfo invokerInfo;

    TaskInfo(InvokerInfo invokerInfo, int level, int levelIndex, int supLevelIndex) {
        this.taskName = invokerInfo.getInvokerName();
        this.level = level;
        this.levelIndex = levelIndex;
        this.supLevelIndex = supLevelIndex;
        this.beginTime = System.currentTimeMillis();
        this.invokerInfo = invokerInfo;
    }

    /**
     * <br>描 述：停止任务
     * <br>作 者：liuqing
     * <br>历 史: (版本) 作者 时间 注释
     */
    void stop() {
        this.endTime = System.currentTimeMillis();
        this.costTime = this.endTime - this.beginTime;
    }

    /**
     * <br>描 述：获取任务耗时
     * <br>作 者：liuqing
     * <br>历 史: (版本) 作者 时间 注释
     *
     * @return
     */
    long getTaskTime() {
        return this.costTime;
    }

    /**
     * Getter for property 'invokerInfo'.
     *
     * @return Value for property 'invokerInfo'.
     */
    public InvokerInfo getInvokerInfo() {
        return invokerInfo;
    }
}
