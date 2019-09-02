package org.wt.log;

import java.util.logging.Level;

public enum LoggerLevel {
    DEBUG(Level.FINE),INFO(Level.INFO),WARN(Level.WARNING),ERROR(Level.SEVERE);
    private Level level;

    LoggerLevel(Level level){
        this.level = level;
    }
    public Level toJULLevel(){
        return level;
    }
    public static LoggerLevel fromJULLevel(Level level){
        LoggerLevel[] ls = LoggerLevel.values();
        for(LoggerLevel ll: ls){
            if(ll.level == level){
                return ll;
            }
        }
        return INFO;
    }
}
