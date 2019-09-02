package org.wt.log;

import java.io.File;
import java.util.logging.Formatter;

public class LoggerConfig {
    private String appName;
    private String logDir;
    private Formatter formatter;
    private LoggerLevel level;
    private LoggerLevel fileHandlerLevel;
    private int appType;
    public static final int APP = 1;
    public static final int WEB = 2;
    public static final int OTHER = 3;

    public static LoggerConfig getDefault(){
        try {
            LoggerConfig config = new LoggerConfig();
            String file = LoggerConfig.class.getClassLoader().getResource("").getFile();
            file = file.replaceAll("\\\\","/");
            config.setLogDir(System.getProperty("user.dir")+"/logs/");
            //    /home/wt/IdeaProjects/cglibtest/target/test-classes/
            int appType = OTHER;
            int end = file.indexOf("/target/");
            if(end>=0){
                appType=APP;
            }else{
                end = file.indexOf("/webapp");
                if(end>=0){
                    appType=WEB;
                }
            }
            int begin = 0;
            if(appType==APP){
                begin = file.lastIndexOf("/",end-1);
            }else if(appType==WEB){
                begin = file.lastIndexOf("/",end-1);
            }else{
                begin = file.lastIndexOf("/");
            }
            config.setAppType(appType);
            String app = file.substring(begin+1,end);
            config.setAppName(app);
            config.setFileHandlerLevel(LoggerLevel.INFO);
            config.setLevel(LoggerLevel.INFO);
            config.setFormatter(new DefaultFormatter());
            return config;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setAppType(int appType) {
        this.appType = appType;
    }

    public int getAppType() {
        return appType;
    }

    public LoggerLevel getFileHandlerLevel() {
        return fileHandlerLevel;
    }

    public void setFileHandlerLevel(LoggerLevel fileHandlerLevel) {
        this.fileHandlerLevel = fileHandlerLevel;
    }

    public LoggerLevel getLevel() {
        return level;
    }

    public void setLevel(LoggerLevel level) {
        this.level = level;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getLogDir() {
        return logDir;
    }

    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    public Formatter getFormatter() {
        return formatter;
    }

    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }
}
