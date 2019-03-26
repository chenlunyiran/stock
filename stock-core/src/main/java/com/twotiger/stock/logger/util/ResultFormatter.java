package com.twotiger.stock.logger.util;

/**
 * 日志记录结果格式化
 * 参考 2017-02-14 10:39:39.467 [http-nio-8080-exec-1] TRACE [org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:136)]  -> Method [demo.web.api.v1.user.login.LoginController.login] returned [UserLoginOutParam{success=false, message='用户不存在'}]
 * Created by liuqing-notebook on 2017/2/13.
 */
public interface ResultFormatter {
    /**
     * @param result 方法调用返回值
     * @return 格式化的字符串
     */
    String format(Object result);
}
