package org.wt.asm.aop;
import groovy.lang.GroovyClassLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Properties;

public abstract class AsmAop {
    public static byte[] transform(String classname, InputStream in) throws Exception{

        // 1. 创建 ClassReader 读入 .class 文件到内存中
        ClassReader reader = new ClassReader(in);
        // 2. 创建 ClassWriter 对象，将操作之后的字节码的字节数组回写
        //ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
        // 3. 创建自定义的 ClassVisitor 对象
        ClassVisitor change = new ClassAopVisitor(writer);
        // 4. 将 ClassVisitor 对象传入 ClassReader 中
        reader.accept(change, ClassReader.EXPAND_FRAMES);
        // 获取修改后的 class 文件对应的字节数组
        byte[] code = writer.toByteArray();
        System.out.println("Success!");
        return code;
    }

    public static void main(String[] args) throws Exception {
        String userDir = System.getProperty("user.dir");
        File f = new File(userDir,"src/test/resources/test.txt");
        System.out.println(f.exists());

        String filename = System.getProperty("user.dir") + "/src/test/resources/org/wt/asm/aop/Demo.class";

        /*if(new File(filename).exists()){
            return;
        }*/

        String className = "org.wt.asm.aop.Demo";
        InputStream in = AsmAop.class.getResourceAsStream("Demo.class");
        byte[] code = transform(className,in);
        new File(filename).getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(filename);    // 将二进制流写到本地磁盘上
        fos.write(code);
        fos.close();
        MyClassLoader cl = new MyClassLoader();
        //GroovyClassLoader cl = new GroovyClassLoader();
        Class clazz = cl.defineClass(className,code);
        //clazz = cl.loadClass(className);
        Object personObj = clazz.newInstance();
        Method nameMethod = clazz.getDeclaredMethod("test", String[].class);
        nameMethod.setAccessible(true);
        MethodAop.map.put(className,new MethodAop(cl,className){
            @Override
            public Object doInvoke(Class clazz, Object target, Method method, Method proceed, Object[] args) throws Throwable {
                return super.doInvoke(clazz, target, method, proceed, args);
            }
        });
        nameMethod.invoke(personObj, new Object[]{new String[]{"a","b","c"}});
    }
}
