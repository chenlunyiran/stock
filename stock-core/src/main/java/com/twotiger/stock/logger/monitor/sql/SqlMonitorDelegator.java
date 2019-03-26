package com.twotiger.stock.logger.monitor.sql;

import com.twotiger.stock.logger.LocalLog;
import net.sf.log4jdbc.Properties;
import net.sf.log4jdbc.log.SpyLogDelegator;
import net.sf.log4jdbc.sql.Spy;
import net.sf.log4jdbc.sql.resultsetcollector.ResultSetCollector;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.StringTokenizer;

/**
 * Created by qing1.liu on 16/11/1.
 */
public class SqlMonitorDelegator implements SpyLogDelegator {
    public SqlMonitorDelegator() {
    }


    private void setSql(String sql) {
        LocalLog.localMessage(sql);
    }


    /**
     * Determine if any of the 5 log4jdbc spy loggers are turned on (jdbc.audit | jdbc.resultset |
     * jdbc.sqlonly | jdbc.sqltiming | jdbc.connection)
     *
     * @return true if any of the 5 spy jdbc/sql loggers are enabled at debug info or error level.
     */
    @Override
    public boolean isJdbcLoggingEnabled() {
        return true;
    }


    @Override
    public void exceptionOccured(Spy spy, String methodCall, Exception e, String sql, long execTime) {
        if (sql == null) {
            setSql("sql is null!");
        } else {
            sql = processSql(sql);
            setSql(sql);
        }
    }

    @Override
    public void methodReturned(Spy spy, String methodCall, String returnMsg) {

    }

    @Override
    public void constructorReturned(Spy spy, String constructionInfo) {
        // not used in this implementation -- yet
    }

    private static String nl = System.getProperty("line.separator");

    /**
     * Determine if the given sql should be logged or not
     * based on the various DumpSqlXXXXXX flags.
     *
     * @param sql SQL to test.
     * @return true if the SQL should be logged, false if not.
     */
    private boolean shouldSqlBeLogged(String sql) {
        if (sql == null) {
            return false;
        }
        sql = sql.trim();

        if (sql.length() < 6) {
            return false;
        }
        return true;
    }

    @Override
    public void sqlOccurred(Spy spy, String methodCall, String sql) {
        if (!Properties.isDumpSqlFilteringOn() || shouldSqlBeLogged(sql)) {
            setSql(processSql(sql));
        }
    }

    /**
     * Break an SQL statement up into multiple lines in an attempt to make it
     * more readable
     *
     * @param sql SQL to break up.
     * @return SQL broken up into multiple lines
     */
    private String processSql(String sql) {
        if (sql == null) {
            return null;
        }

        if (Properties.isSqlTrim()) {
            sql = sql.trim();
        }

        StringBuilder output = new StringBuilder();

        if (Properties.getDumpSqlMaxLineLength() <= 0) {
            output.append(sql);
        } else {
            // insert line breaks into sql to make it more readable
            StringTokenizer st = new StringTokenizer(sql);
            String token;
            int linelength = 0;

            while (st.hasMoreElements()) {
                token = (String) st.nextElement();

                output.append(token);
                linelength += token.length();
                output.append(" ");
                linelength++;
                if (linelength > Properties.getDumpSqlMaxLineLength()) {
                    output.append(nl);
                    linelength = 0;
                }
            }
        }

        if (Properties.isDumpSqlAddSemicolon()) {
            output.append(";");
        }

        String stringOutput = output.toString();

        if (Properties.isTrimExtraBlankLinesInSql()) {
            LineNumberReader lineReader = new LineNumberReader(new StringReader(stringOutput));

            output = new StringBuilder();

            int contiguousBlankLines = 0;
            try {
                while (true) {
                    String line = lineReader.readLine();
                    if (line == null) {
                        break;
                    }

                    // is this line blank?
                    if (line.trim().length() == 0) {
                        contiguousBlankLines++;
                        // skip contiguous blank lines
                        if (contiguousBlankLines > 1) {
                            continue;
                        }
                    } else {
                        contiguousBlankLines = 0;
                        output.append(line);
                    }
                    output.append(nl);
                }
            } catch (IOException e) {
                // since we are reading from a buffer, this isn't likely to happen,
                // but if it does we just ignore it and treat it like its the end of the stream
            }
            stringOutput = output.toString();
        }

        return stringOutput;
    }

    /**
     * Special call that is called only for JDBC method calls that contain SQL.
     *
     * @param spy        the Spy wrapping the class where the SQL occurred.
     * @param execTime   how long it took the SQL to run, in milliseconds.
     * @param methodCall a description of the name and call parameters of the
     *                   method that generated the SQL.
     * @param sql        SQL that occurred.
     */
    @Override
    public void sqlTimingOccurred(Spy spy, long execTime, String methodCall, String sql) {

    }

    @Override
    public void debug(String msg) {

    }

    @Override
    public void connectionOpened(Spy spy, long execTime) {
        //we just delegate to the already existing method,
        //so that we do not change the behavior of the standard implementation
        this.connectionOpened(spy);
    }

    /**
     * Called whenever a new connection spy is created.
     *
     * @param spy ConnectionSpy that was created.
     */
    private void connectionOpened(Spy spy) {

    }

    @Override
    public void connectionClosed(Spy spy, long execTime) {
        //we just delegate to the already existing method,
        //so that we do not change the behavior of the standard implementation
        this.connectionClosed(spy);
    }

    /**
     * Called whenever a connection spy is closed.
     *
     * @param spy ConnectionSpy that was closed.
     */
    private void connectionClosed(Spy spy) {

    }

    @Override
    public void connectionAborted(Spy spy, long execTime) {
        this.connectionAborted(spy);
    }

    /**
     * Called whenever a connection spy is aborted.
     *
     * @param spy ConnectionSpy that was aborted.
     */
    private void connectionAborted(Spy spy) {

    }

    @Override
    public boolean isResultSetCollectionEnabled() {
        return false;
    }

    @Override
    public boolean isResultSetCollectionEnabledWithUnreadValueFillIn() {
        return false;
    }

    @Override
    public void resultSetCollected(ResultSetCollector resultSetCollector) {

    }
}
