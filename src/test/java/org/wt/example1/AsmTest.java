package org.wt.example1;

import org.objectweb.asm.*;

import java.io.*;
/*
public class AsmTest {
    public static void main(String[] args) throws Exception {
        String filename = System.getProperty("user.dir") + "/src/test/resources/org/wt/"+MyInvoker.example()+"/Demo.class";
        File file = new File(filename);
        file.getParentFile().mkdirs();
        ClassReader cr = new ClassReader(Demo.class.getName().replaceAll("\\\\","/"));
        //ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassAdapter classAdapter = new AddTimeClassAdapter(cw);
        //使给定的访问者访问Java类的ClassReader
        cr.accept(classAdapter, ClassReader.SKIP_DEBUG);
        byte[] data = cw.toByteArray();

        FileOutputStream fout = new FileOutputStream(file);
        fout.write(data);
        fout.close();
        System.out.println("success!");
    }
}*/
