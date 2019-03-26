package com.twotiger.stock.logger.constant;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2016/1/27
 * Time: 21:29
 * To change this template use File | Settings | File Templates.
 */
public final class ConstantLog {
    private ConstantLog(){}

    /**
     * 系统_监控_计时 日志
     */
    public static final Logger MONITOR_COSTTIME_LOG = LogManager.getLogger("system.monitor.costtime");

    /**
     * 系统_消息_推送 日志
     */
    public static final Logger MESSAGE_PUSH_LOG = LogManager.getLogger("system.message.push");

    /**
     * 系统_消息_短信 日志
     */
    public static final Logger MESSAGE_SMS_LOG = LogManager.getLogger("system.message.sms");

    /**
     * 系统_消息_邮件 日志
     */
    public static final Logger MESSAGE_EMAIL_LOG = LogManager.getLogger("system.message.email");

    /**
     * 系统_任务_ 日志
     */
    public static final Logger MESSAGE_TASK_LOG = LogManager.getLogger("system.task.quartz");
}
