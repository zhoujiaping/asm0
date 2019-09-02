package org.wt.example2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * every class, use a Invoker to intercept all methods.
 */
public class MyInvoker {
    public static String TYPE = "L"+MyInvoker.class.getName().replaceAll("\\.","/")+";";
    public static Map<String,MyInvoker> map = new ConcurrentHashMap<>();
    private boolean enable = true;
    private String className;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public MyInvoker(String clazz){
        this.className = clazz;
        map.put(clazz,this);
    }

    public static Object ivk(){
        //return new Date();
        return null;
    }
    public Object invoke(Object target,String method,String[] pts,Object[] args)throws Throwable{
        //return new Date();
        return null;
    }
}
