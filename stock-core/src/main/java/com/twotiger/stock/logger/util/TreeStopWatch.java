package com.twotiger.stock.logger.util;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * <br>类 名: TreeStopWatch
 * <br>描 述: 可重入的计时器 支持树状结构日志打印,一次打印统计, 非线程安全
 * <br>作 者: liuqing
 * <br>创 建： 2015年3月10日
 * <br>版 本：1.0.0
 * <br>支持
 * <br>     start()
 * <br>             start()
 * <br>                     start()stop()
 * <br>                     start()stop()
 * <br>              stop()
 * <br>     stop()
 * <br>结构的调用
 * <br>需要保证对称性
 * <br>历 史: (版本) 作者 liuqing 20150702 注释 优化项 1 层级自动扩展 2 层溢出策略
 * <br>历 史: (版本) 作者 liuqing 20170202 注释 优化项 1 stringbuilder初始化大小的动态优化 2 层级日志支持  3 调用上下文  4 入参出参格式化
 */
public final class TreeStopWatch {

//	public static void main(String[] args) throws InterruptedException {
//		TreeStopWatch tsw = new TreeStopWatch(6);
//		tsw.start("1");
//			tsw.start("1-1");
//				tsw.start("1-1-1");
//				Thread.sleep(17L);
//				tsw.stop();
//				tsw.start("1-1-2");
//				tsw.stop();
//				tsw.start("1-1-3");
//				tsw.stop();
//				tsw.start("1-1-4");
//				tsw.start("1-1-4-1");
//				tsw.start("1-1-4-1-1");
//				tsw.start("1-1-4-1-1-1");
//				tsw.start("1-1-4-1-1-1-111");
//				tsw.start("1-1-4-1-1-1-111-2222");
//				tsw.start("1-1-4-1-1-1-111-2222-33333");
//				tsw.setError("error");
//				tsw.stop();
//				tsw.stop();
//				tsw.stop();
//				tsw.stop();
//				tsw.stop();
//				tsw.stop();
//				tsw.stop();
//			tsw.stop();
//			tsw.start("1-2");
//			Thread.sleep(360L);
//				tsw.start("1-2-1");
//					tsw.start("1-2-1-1");
//					Thread.sleep(180L);
//					tsw.setError("1-2-1-1 error!");
//					tsw.stop();
//					Thread.sleep(37L);
//					tsw.start("1-2-1-2");
//					tsw.stop();
//				Thread.sleep(100L);
//				tsw.stop();
//				tsw.start("1-2-2");
//				tsw.setError("error by 111");
//				tsw.stop();
//			tsw.stop();
//		tsw.stop();
//		System.out.println(tsw.formatResult());
//		System.out.println(tsw.formatResult());
//		tsw.start("1");
//		Thread.sleep(102L);
//		tsw.stop();
//		System.out.println(tsw.formatResult());
//	}
public static final int DEFAULT_LEVEL_COUNT = 6;

    public static final int MAX_LEVEL_COUNT = 6;

    private final static Logger LOG = LogManager.getLogger(TreeStopWatch.class);

    /**
     * @param levelCount 最大层数  范围[1,6] 超出的不会统计
     */
    public TreeStopWatch(int levelCount) {
        if (levelCount > 6 || levelCount < 1) {
            throw new IllegalStateException("任务结构最多支持6层,最少1层!");
        }
        this.levelCount = levelCount;
        this.taskList = new ArrayList<>(levelCount);
    }

    /**
     * 未设置调用名称时的默认值
     */
    private static final String DEFAULT_CALL_NAME = "unset";
    /**
     * 未设置调用标识时的默认值
     */
    private static final String DEFAULT_CALL_TOKEN = "token";
    /**
     * 调用时间百分比符号
     */
    private static final String RATIO_TOKEN = "%";
    /**
     * 换行符号
     */
    private static final String BR = "\n";
    /**
     * 层级格式化前缀样式
     */
    private static final String FORMAT_0 = "1-----";
    private static final String FORMAT_1 = "2----------";
    private static final String FORMAT_2 = "3---------------";
    private static final String FORMAT_3 = "4--------------------";
    private static final String FORMAT_4 = "5-------------------------";
    private static final String FORMAT_5 = "6------------------------------";
    private static final String FORMAT_6 = "7----------------------------------";
    private static final String LEVEL_FORMAT_0 = FORMAT_0.replace("1----", "     ").replaceFirst("-", "|").replace("-", "_");
    private static final String LEVEL_FORMAT_1 = FORMAT_1.replace("2----", "     ").replaceFirst("-", "|").replace("-", "_");
    private static final String LEVEL_FORMAT_2 = FORMAT_2.replace("3----", "     ").replaceFirst("-", "|").replace("-", "_");
    private static final String LEVEL_FORMAT_3 = FORMAT_3.replace("4----", "     ").replaceFirst("-", "|").replace("-", "_");
    private static final String LEVEL_FORMAT_4 = FORMAT_4.replace("5----", "     ").replaceFirst("-", "|").replace("-", "_");
    private static final String LEVEL_FORMAT_5 = FORMAT_5.replace("6----", "     ").replaceFirst("-", "|").replace("-", "_");
    private static final String LEVEL_FORMAT_6 = FORMAT_6.replace("7----", "     ").replaceFirst("-", "|").replace("-", "_");
    private static final String ROOT_FORMAT = "编号root 任务:%s 总耗时:%d ms 总个数：%d";
    private static final String CHILD_FORMAT = "编号%d-%d 任务:%s 参数: %s 结果 :%s 耗时:%d ms  百分比:%s  异常信息：%s";
    private static final String LAYER_FORMAT = "本层调用总耗时:%d ms  百分比:%s";
    /**
     * 开始样式
     */
    private static final String BEGIN_STYLE = "---------------------------begin-------------------------";
    /**
     * 结束样式
     */
    private static final String END_STYLE = "---------------------------end---------------------------";

    /**
     * 支持最大层级数
     */
    private final int levelCount;

    /**
     * 当前已经扩展到的层级，用于支持动态扩展 extendLevel<=levelCount
     */
    private int extendLevel = 0;
    /**
     * 任务列表
     */
    private final ArrayList<LinkedList<TaskInfo>> taskList;

    /**
     * 请求名称
     */
    private String callName = DEFAULT_CALL_NAME;

    /**
     * 请求标识
     */
    private String callToken = DEFAULT_CALL_TOKEN;

    /**
     * 执行的任务级别 默认0 根节点   taskLevel<5 最多支持5层结构
     */
    private int taskLevel = -1;

    /**
     * 总任务数(调用stop的次数)
     */
    private int taskCount = 0;

    /**
     * 是否有错误
     */
    private boolean hasError = false;

    private Throwable error = null;

    /**
     * 超时时间
     */
    private long timeOut = 1000L;

    /**
     * 是否监控 层级日志
     */
    private boolean monitorLevel = true;

    /**
     * 方法调用参数格式化
     */
    private ParamFormatter paramFormatter = new SimpleParamFormatter();

    /**
     * 方法调用结果格式化
     */
    private ResultFormatter resultFormatter = new NoneResultFormatter();

    /**
     * <br>描 述：设置请求名称（调用链的开始）
     * <br>作 者：liuqing
     * <br>历 史: (版本) 作者 时间 注释
     *
     * @param callName
     */
    public void setCallName(String callName) {
        this.callName = callName;
    }

    /**
     * <br>描 述：重设计时器
     * <br>作 者：liuqing
     * <br>历 史: (版本) 作者 时间 注释
     */
    private void reset() {
        taskList.clear();
        extendLevel = 0;
        callName = DEFAULT_CALL_NAME;
        taskLevel = -1;
        taskCount = 0;
        callToken = DEFAULT_CALL_TOKEN;
        hasError = false;
    }

    /**
     * 设置请求标识
     *
     * @param callToken
     */
    public void setCallToken(String callToken) {
        this.callToken = callToken;
    }


    /**
     * <br>任务启动
     *
     * @param invokerInfo 调用名称
     * @throws IllegalStateException
     */
    public void start(InvokerInfo invokerInfo) {
        taskLevel++;
        if (taskLevel >= levelCount) {//层溢出了
            LOG.error("任务结构最多支持{}层!{}", levelCount, invokerInfo.getInvokerName());
        } else {
            if (taskLevel == extendLevel) {//动态扩展
                taskList.add(new LinkedList<>());
                extendLevel++;
            }
            taskList.get(taskLevel).addLast(new TaskInfo(invokerInfo, taskLevel + 1, taskList.get(taskLevel).size() + 1, taskLevel == 0 ? 0 : taskList.get(taskLevel - 1).size()));
        }
    }

    /**
     * <br>结束上次启动的任务
     *
     * @throws IllegalStateException
     */
    public void stop() {
        if (taskLevel == -1) {
            throw new IllegalStateException("任务调用非对称,请先调用start()!");
        }
        if (taskLevel < levelCount) {//溢出的层不统计
            taskList.get(taskLevel).getLast().stop();
            ++this.taskCount;
        }
        taskLevel--;
    }

    /**
     * <br>描 述：记录当前调用中的异常信息  注意事项需要在当前的.start（）和.stop()方法之间调用
     * <br>作 者：liuqing
     * <br>历 史: (版本) 作者 时间 注释
     *
     * @param error
     */
    public void setError(Throwable error) {
        this.hasError = true;
        this.error = error;
        if (taskLevel < levelCount) {//溢出的层不统计
            taskList.get(taskLevel).getLast().errorMessage = error.getMessage();
        }
    }

    /**
     * 设置方法执行结果
     *
     * @param result
     */
    public void setResult(Object result) {
        if (taskLevel < levelCount) {//溢出的层不统计
            taskList.get(taskLevel).getLast().result = result;
        }
    }

    /**
     * 增加层级日志
     *
     * @param logMessage
     */
    public void addLog(String logMessage) {
        if (monitorLevel && taskLevel < levelCount) {//溢出的层不统计
            LinkedList<String> logs = taskList.get(taskLevel).getLast().logs;
            if (logs == null) {//init logs
                logs = new LinkedList<>();
                taskList.get(taskLevel).getLast().logs = logs;
            }
            logs.add(logMessage);
        }
    }


    /**
     * 获取调用类名称
     *
     * @return
     */
    public String getInvokerClassName() {
        if (taskLevel < levelCount) {//溢出的层不统计
            return taskList.get(taskLevel).getLast().getInvokerInfo().getInvokerClassName();
        }
        return null;
    }

    /**
     * 获取调用方法名称
     *
     * @return
     */
    public String getInvokerMethodName() {
        if (taskLevel < levelCount) {//溢出的层不统计
            return taskList.get(taskLevel).getLast().getInvokerInfo().getInvokerMethodName();
        }
        return null;
    }

    /**
     * 是否有错误信息
     *
     * @return true 有  false 无
     */
    public boolean hasError() {
        return hasError;
    }

    /**
     * 判断是否超时
     *
     * @return
     */
    public boolean isTimeOut() {
        return taskList.get(0).get(0).getTaskTime() > timeOut;
    }

    /**
     * 设置超时时间
     *
     * @param timeOut
     */
    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    public void setParamFormatter(ParamFormatter paramFormatter) {
        this.paramFormatter = paramFormatter;
    }

    public void setResultFormatter(ResultFormatter resultFormatter) {
        this.resultFormatter = resultFormatter;
    }

    public void setMonitorLevel(boolean monitorLevel) {
        this.monitorLevel = monitorLevel;
    }

    public Throwable getError() {
        return error;
    }

    /**
     * <br>返回按树结构格式化的统计信息 ， 在最外层stop后使用输出日志信息
     *
     * @return 统计计时信息
     */
    public String formatResult() {
        if (taskLevel != -1) {
            throw new IllegalStateException("任务调用未结束,不能统计!");
        }
        int bufferSize = 256 * extendLevel; //StringBuilder 优化
        StringBuilder stringBuilder = new StringBuilder(bufferSize);
        stringBuilder.append(BR).append("开始调用：").append(this.callName).append("  call id:").append(callToken).append(BEGIN_STYLE).append(BR);
        long allCostTime = 0;//总耗时
        double allCostTime_100 = 0;// allCostTime/100 计算一次
        double ratio = 0;//百分比
        long levevCostTime = 0;//层级调用耗时
        for (int i = 0; i < extendLevel; i++) {
            for (TaskInfo taskInfo : taskList.get(i)) {
                if (i == 0) {//总任务
                    allCostTime = taskInfo.getTaskTime();//总耗时
                    allCostTime_100 = allCostTime / 100D;
                    stringBuilder.append(format(i)).append(String.format(ROOT_FORMAT, taskInfo.taskName, allCostTime, taskCount)).append(BR);
                } else {//子任务
                    levevCostTime += taskInfo.costTime;
                    if (allCostTime_100 != 0) {//java.lang.ArithmeticException: / by zero
                        ratio = taskInfo.costTime / allCostTime_100;
                    } else {
                        ratio = 0;
                    }
                    stringBuilder.append(format(i)).append(String.format(
                        CHILD_FORMAT,
                        taskInfo.supLevelIndex,
                        taskInfo.levelIndex,
                        taskInfo.taskName,
                        paramFormatter.format(taskInfo.invokerInfo.getArgs()),
                        resultFormatter.format(taskInfo.result),
                        taskInfo.getTaskTime(),
                        (int) ratio + RATIO_TOKEN,
                        taskInfo.errorMessage)
                    ).append(BR);
                    if (taskInfo.logs != null) {
                        for (String logMessage : taskInfo.logs) {
                            stringBuilder.append(levelFormat(i)).append(" msg: ").append(logMessage).append(BR);
                        }
                    }
                }
            }
            if (i > 0) {
                if (allCostTime_100 != 0) {
                    ratio = levevCostTime / allCostTime_100;
                } else {
                    ratio = 0;
                }
                stringBuilder.append(format(i)).append(String.format(LAYER_FORMAT, levevCostTime, (int) ratio + RATIO_TOKEN)).append(BR);
            }
            levevCostTime = 0;
        }
        stringBuilder.append("结束调用：").append(this.callName).append("  call id:").append(callToken).append(END_STYLE).append(BR);
        reset();//clear gc TaskInfo
        String result = stringBuilder.toString();
        return result;
    }

    /**
     * <br>描 述：生成前缀
     * <br>作 者：liuqing
     * <br>历 史: (版本) 作者 时间 注释
     *
     * @param level
     * @return
     */
    private String format(int level) {
        switch (level) {
            case 0:
                return FORMAT_0;
            case 1:
                return FORMAT_1;
            case 2:
                return FORMAT_2;
            case 3:
                return FORMAT_3;
            case 4:
                return FORMAT_4;
            case 5:
                return FORMAT_5;
            case 6:
                return FORMAT_6;
            default:
                return "";
        }
    }

    /**
     * <br>描 述：层级生成前缀
     * <br>作 者：liuqing
     * <br>历 史: (版本) 作者 时间 注释
     *
     * @param level
     * @return
     */
    private String levelFormat(int level) {
        switch (level) {
            case 0:
                return LEVEL_FORMAT_0;
            case 1:
                return LEVEL_FORMAT_1;
            case 2:
                return LEVEL_FORMAT_2;
            case 3:
                return LEVEL_FORMAT_3;
            case 4:
                return LEVEL_FORMAT_4;
            case 5:
                return LEVEL_FORMAT_5;
            case 6:
                return LEVEL_FORMAT_6;
            default:
                return "";
        }
    }
}
