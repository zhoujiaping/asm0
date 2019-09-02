package org.wt;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.wt.log.Log;
import org.wt.log.LoggerConfig;
import org.wt.log.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class LogTest {
    @Test
    public void test1(){
        Logger log = Logger.getLogger(this.getClass().getName());
        log.setLevel(Level.INFO);
        log.info("hello world");
    }
    @Test
    public void test2() throws IOException {
        String app = "cglibtest";
        Logger log = Logger.global;
        log.setLevel(Level.ALL);

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        String date = fmt.format(new Date());
        FileHandler fileHandler = new FileHandler("/home/wt/logs/"+app+"/"+app+"_"+date+"_%g.log",10000,100,true);
        fileHandler.setEncoding("utf-8");
        fileHandler.setLevel(Level.INFO);
        log.addHandler(fileHandler);

        fileHandler.setFormatter(new MyLogHander());

        log.info("hello world");
        log.log(Level.SEVERE,"hello world",new RuntimeException(""));
    }
    class MyLogHander extends Formatter {
        @Override
        public String format(LogRecord record) {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String date = fmt.format(new Date());
            Level level = record.getLevel();
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
            String text = left+date+sep+level.toString().substring(0,4)+sep+pid+sep+seq+right+" "
                    +left+loggerName+sep+cn+sep+mn+right+" "+msg+errMsg;
            return text;
        }
    }
    @Test
    public void test3(){
        String file = LoggerConfig.class.getClassLoader().getResource("").getFile();
        System.out.println(file);
        System.out.println("/a/b/c/d".lastIndexOf("/",3));
        int end = file.indexOf("/target/");
        if(end<0){
            end = file.indexOf("/webapp");
        }
        if(end<0){
            System.out.println("unknown");
        }else{
            int begin = file.lastIndexOf("/",end-1);
            String app = file.substring(begin+1,end);
            System.out.println(app);
        }
    }
    @Test
    public void test4(){
        Log.info("hello");
    }
}
