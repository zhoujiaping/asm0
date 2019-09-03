package org.wt.asm.aop;

import org.wt.asm.util.ClassUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * every class, use a Invoker to intercept all methods.
 */
public class MethodAop {
    public static final String SUFFIX = "_pxy";
    public static Map<String,MethodAop> map = new ConcurrentHashMap<>();
    private String className;

    public MethodAop(String clazz){
        this.className = clazz;
        map.put(clazz,this);
    }

    public Object invoke(Object target, String methodName, String[] pts,Object... args) throws Throwable {
        System.out.println("invoke");
        Class clazz = Class.forName(className);
        Method method;
        Method proceed;
        if(pts!=null && pts.length>0) {
            Class[] ptClasses = new Class[pts.length];
            for(int i=0;i<pts.length;i++) {
                Class c = ClassUtil.forName(pts[i]);
                ptClasses[i] = c;
            }
            method = clazz.getDeclaredMethod(methodName,ptClasses);
            proceed = clazz.getDeclaredMethod(methodName+MethodAop.SUFFIX,ptClasses);
        }else {
            proceed = clazz.getDeclaredMethod(methodName+MethodAop.SUFFIX);
            method = clazz.getDeclaredMethod(methodName);
        }
        if(!proceed.isAccessible()){
            proceed.setAccessible(true);
        }
        return doInvoke(clazz,target,method,proceed,args);
    }

    public Object doInvoke(Class clazz, Object target, Method method, Method proceed, Object[] args) throws Throwable {
        return proceed.invoke(target,args);
    }

}
