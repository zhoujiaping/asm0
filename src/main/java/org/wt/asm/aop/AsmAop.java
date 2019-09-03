package org.wt.asm.aop;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.*;

public abstract class AsmAop {
    public static byte[] transform(String classname, InputStream in) throws Exception{
        // 1. 创建 ClassReader 读入 .class 文件到内存中
        ClassReader reader = new ClassReader(in);
        // 2. 创建 ClassWriter 对象，将操作之后的字节码的字节数组回写
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
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
        String className = AsmAop.class.getName();
        InputStream in = AsmAop.class.getResourceAsStream("AsmAop.class");
        byte[] code = transform(className,in);
        String filename = System.getProperty("user.dir") + "/src/test/resources/org/wt/AsmAop.class";
        new File(filename).getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(filename);    // 将二进制流写到本地磁盘上
        fos.write(code);
        fos.close();
    }
}
