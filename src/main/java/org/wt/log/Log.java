package org.wt.log;

import java.util.ArrayList;
import java.util.List;

public class Log {
    private static class LoggerHolder{
        public static Logger logger = new Logger(LoggerConfig.getDefault());
    }
    public static void debug(String msg,Object... args){
        LoggerHolder.logger.debug(msg,args);
    }
    public static void info(String msg,Object... args){
        LoggerHolder.logger.info(msg,args);
    }
    public static void warn(String msg,Object... args){
        LoggerHolder.logger.warn(msg,args);
    }
    public static void error(String msg,Object... args){
        //logger.error(msg,args);
    }
    public static void main(String[] args) {
        String tmpl = "public static void error(String msg, {},Throwable e) {" +
                "LoggerHolder.logger.error(msg,{},e);" +
                "}";
        for(int n=1;n<20;n++){
            List<String> var1 = new ArrayList<>();
            List<String> var2 = new ArrayList<>();
            for(int i=0;i<n;i++){
                var1.add("Object arg"+i);
                var2.add("arg"+i);
            }
            String str = StringFormatter.format(tmpl,String.join(",",var1),String.join(",",var2));
            System.out.println(str);
        }
    }
    public static void error(String msg,Throwable e) {LoggerHolder.logger.error(msg,e);}
    public static void error(String msg, Object arg0,Throwable e) {LoggerHolder.logger.error(msg,arg0,e);}
    public static void error(String msg, Object arg0,Object arg1,Throwable e) {LoggerHolder.logger.error(msg,arg0,arg1,e);}
    public static void error(String msg, Object arg0,Object arg1,Object arg2,Throwable e) {LoggerHolder.logger.error(msg,arg0,arg1,arg2,e);}
    public static void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Throwable e) {LoggerHolder.logger.error(msg,arg0,arg1,arg2,arg3,e);}
    public static void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Throwable e) {LoggerHolder.logger.error(msg,arg0,arg1,arg2,arg3,arg4,e);}
    public static void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Throwable e) {LoggerHolder.logger.error(msg,arg0,arg1,arg2,arg3,arg4,arg5,e);}
    public static void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Throwable e) {LoggerHolder.logger.error(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,e);}
    public static void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Throwable e) {LoggerHolder.logger.error(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,e);}
    public static void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Throwable e) {LoggerHolder.logger.error(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,e);}
    public static void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Throwable e) {LoggerHolder.logger.error(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,e);}
    public static void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Throwable e) {LoggerHolder.logger.error(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,e);}
    public static void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Throwable e) {LoggerHolder.logger.error(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,e);}
    public static void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Object arg12,Throwable e) {LoggerHolder.logger.error(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12,e);}
    public static void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Object arg12,Object arg13,Throwable e) {LoggerHolder.logger.error(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12,arg13,e);}
    public static void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Object arg12,Object arg13,Object arg14,Throwable e) {LoggerHolder.logger.error(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12,arg13,arg14,e);}
    public static void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Object arg12,Object arg13,Object arg14,Object arg15,Throwable e) {LoggerHolder.logger.error(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12,arg13,arg14,arg15,e);}
    public static void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Object arg12,Object arg13,Object arg14,Object arg15,Object arg16,Throwable e) {LoggerHolder.logger.error(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12,arg13,arg14,arg15,arg16,e);}
    public static void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Object arg12,Object arg13,Object arg14,Object arg15,Object arg16,Object arg17,Throwable e) {LoggerHolder.logger.error(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12,arg13,arg14,arg15,arg16,arg17,e);}
    public static void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Object arg12,Object arg13,Object arg14,Object arg15,Object arg16,Object arg17,Object arg18,Throwable e) {LoggerHolder.logger.error(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12,arg13,arg14,arg15,arg16,arg17,arg18,e);}

}
