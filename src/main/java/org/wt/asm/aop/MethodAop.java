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
	//internal class name
    public static final String SUFFIX = "_pxy";
    public static final String MAP_NAME = "map";
    public static final String MAP_DESC = "Ljava/util/Map;";
    public static final String INVOKE_METHOD = "invoke";

    public static final String ICN = MethodAop.class.getName().replaceAll("\\.","/");
    public static Map<String,MethodAop> map = new ConcurrentHashMap<>();
    private ClassLoader cl;
    private String className;

    public MethodAop(ClassLoader cl,String clazz){
        this.cl = cl;
        this.className = clazz;
        map.put(clazz,this);
    }

    public Object invoke(Object target, String methodName, String[] pts,Object... args) throws Throwable {
        System.out.println("invoke");
        Class clazz = ClassUtil.forName(className,true,cl);
        Method method;
        Method proceed;
        if(pts!=null && pts.length>0) {
            Class[] ptClasses = new Class[pts.length];
            for(int i=0;i<pts.length;i++) {
                Class c = ClassUtil.forName(pts[i],true,cl);
                ptClasses[i] = c;
            }
            method = clazz.getDeclaredMethod(methodName,ptClasses);
            proceed = clazz.getDeclaredMethod(methodName+MethodAop.SUFFIX,ptClasses);
        }else {
            method = clazz.getDeclaredMethod(methodName);
            proceed = clazz.getDeclaredMethod(methodName+MethodAop.SUFFIX);
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
