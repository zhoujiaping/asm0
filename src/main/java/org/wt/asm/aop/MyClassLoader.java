package org.wt.asm.aop;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MyClassLoader extends ClassLoader {
    protected Map<String,Class> loadedClasses = new ConcurrentHashMap<>();
    /*@Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class c = loadedClasses.get(name);
        if(c!=null){
            return c;
        }

        c = super.loadClass(name, resolve);
        if(c == null){
            getClassCode(name);
            c = super.defineClass(name,code,0,len);
        }

        if(c == null){
            throw new ClassNotFoundException("class not found: "+name);
        }
        loadedClasses.put(name,c);
        return c;
    }*/
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class c = loadedClasses.get(name);
        if(c!=null){
            return c;
        }
        byte[] code = getClassCode(name);
        if(code != null){
            c = super.defineClass(name,code,0,code.length);
            loadedClasses.put(name,c);
            return c;
        }
        c = super.loadClass(name, resolve);
        if(c == null){
            throw new ClassNotFoundException("class not found: "+name);
        }
        loadedClasses.put(name,c);
        return c;
    }

    private byte[] getClassCode(String name) {
        return null;
    }
    public Class defineClass(String name,byte[] code){
        return super.defineClass(name,code,0,code.length);
    }

}
