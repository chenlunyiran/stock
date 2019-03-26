package com.twotiger.stock.logger.appender;

import com.twotiger.stock.logger.LocalLog;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;

import java.io.Serializable;

/**
 * http://blog.csdn.net/z69183787/article/details/51776323
 * 本地上下文 Appender
 * Created by liuqing on 2017/1/10.
 */
public class LocalContextAppender extends AbstractAppender {

    protected LocalContextAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
        super(name, filter, layout);
    }

    protected LocalContextAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    @Override
    public void append(LogEvent logEvent) {
        String logMessage = ((AbstractStringLayout) this.getLayout()).toSerializable(logEvent);
        if(LocalLog.isEnabled()){
            LocalLog.localMessage(logMessage);
        }
    }
}
