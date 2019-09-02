package org.wt.log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class Logger {
    private java.util.logging.Logger log;
    protected Logger(LoggerConfig config){
        try{
            String app = config.getAppName();
            log = java.util.logging.Logger.global;
            log.setLevel(config.getLevel().toJULLevel());

            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            String date = fmt.format(new Date());
            new File(config.getLogDir()).mkdirs();
            FileHandler fileHandler = new FileHandler(config.getLogDir()+"/"+app+"_"+date+"_%g.log",10000,100,true);
            fileHandler.setEncoding("utf-8");
            fileHandler.setLevel(config.getFileHandlerLevel().toJULLevel());
            log.addHandler(fileHandler);

            fileHandler.setFormatter(config.getFormatter());
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public void debug(String msg, Object... args) {
        log.fine(StringFormatter.format(msg,args));
    }

    public void info(String msg, Object... args) {
        log.info(StringFormatter.format(msg,args));
    }

    public void warn(String msg, Object... args) {
        log.log(Level.WARNING,StringFormatter.format(msg,args));
    }


    public static void main(String[] args) {
        String tmpl = "public void error(String msg, {},Throwable e) {" +
                "log.log(Level.SEVERE,StringFormatter.format(msg,{}),e);" +
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

    public void error(String msg, Throwable e) {log.log(Level.SEVERE,msg,e);}

    public void error(String msg, Object arg0,Throwable e) {log.log(Level.SEVERE,StringFormatter.format(msg,arg0),e);}
    public void error(String msg, Object arg0,Object arg1,Throwable e) {log.log(Level.SEVERE,StringFormatter.format(msg,arg0,arg1),e);}
    public void error(String msg, Object arg0,Object arg1,Object arg2,Throwable e) {log.log(Level.SEVERE,StringFormatter.format(msg,arg0,arg1,arg2),e);}
    public void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Throwable e) {log.log(Level.SEVERE,StringFormatter.format(msg,arg0,arg1,arg2,arg3),e);}
    public void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Throwable e) {log.log(Level.SEVERE,StringFormatter.format(msg,arg0,arg1,arg2,arg3,arg4),e);}
    public void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Throwable e) {log.log(Level.SEVERE,StringFormatter.format(msg,arg0,arg1,arg2,arg3,arg4,arg5),e);}
    public void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Throwable e) {log.log(Level.SEVERE,StringFormatter.format(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6),e);}
    public void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Throwable e) {log.log(Level.SEVERE,StringFormatter.format(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7),e);}
    public void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Throwable e) {log.log(Level.SEVERE,StringFormatter.format(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8),e);}
    public void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Throwable e) {log.log(Level.SEVERE,StringFormatter.format(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9),e);}
    public void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Throwable e) {log.log(Level.SEVERE,StringFormatter.format(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10),e);}
    public void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Throwable e) {log.log(Level.SEVERE,StringFormatter.format(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11),e);}
    public void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Object arg12,Throwable e) {log.log(Level.SEVERE,StringFormatter.format(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12),e);}
    public void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Object arg12,Object arg13,Throwable e) {log.log(Level.SEVERE,StringFormatter.format(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12,arg13),e);}
    public void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Object arg12,Object arg13,Object arg14,Throwable e) {log.log(Level.SEVERE,StringFormatter.format(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12,arg13,arg14),e);}
    public void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Object arg12,Object arg13,Object arg14,Object arg15,Throwable e) {log.log(Level.SEVERE,StringFormatter.format(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12,arg13,arg14,arg15),e);}
    public void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Object arg12,Object arg13,Object arg14,Object arg15,Object arg16,Throwable e) {log.log(Level.SEVERE,StringFormatter.format(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12,arg13,arg14,arg15,arg16),e);}
    public void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Object arg12,Object arg13,Object arg14,Object arg15,Object arg16,Object arg17,Throwable e) {log.log(Level.SEVERE,StringFormatter.format(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12,arg13,arg14,arg15,arg16,arg17),e);}
    public void error(String msg, Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Object arg12,Object arg13,Object arg14,Object arg15,Object arg16,Object arg17,Object arg18,Throwable e) {log.log(Level.SEVERE,StringFormatter.format(msg,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12,arg13,arg14,arg15,arg16,arg17,arg18),e);}

}
