package org.wt.example2;

import java.lang.invoke.*;

public class Bootstrap {
    public static Object ivk(String clazz,String method,String[] pts,Object[] args) {
        System.out.println("Hello!");
        return null;
    }

    public static CallSite bootstrap(MethodHandles.Lookup caller, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {


        MethodHandles.Lookup lookup = MethodHandles.lookup();
        Class thisClass = lookup.lookupClass();
        MethodHandle mh = lookup.findStatic(thisClass, "hello", MethodType.methodType(void.class));
        return new ConstantCallSite(mh.asType(type));
    }
}
