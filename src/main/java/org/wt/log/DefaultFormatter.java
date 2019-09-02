package org.wt.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class DefaultFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String date = fmt.format(new Date());
        Level level = record.getLevel();
        LoggerLevel loggerLevel = LoggerLevel.fromJULLevel(level);
        String msg = record.getMessage();
        String loggerName = record.getLoggerName();
        //record.getParameters();
        long seq = record.getSequenceNumber();
        String cn = record.getSourceClassName();
        String mn = record.getSourceMethodName();
        int pid = record.getThreadID();
        Throwable e = record.getThrown();
        String errMsg = "\n";
        if(e!=null){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            errMsg+=sw.toString()+"\n";
        }
        //return record.getLevel() + ":" + record.getMessage()+"\n";
        String left = "[";
        String sep = "-";
        String right = "]";
        String text = left+date+sep+loggerLevel+sep+pid+sep+seq+right+" "
                +left+loggerName+sep+cn+sep+mn+right+" "+msg+errMsg;
        return text;
    }
}