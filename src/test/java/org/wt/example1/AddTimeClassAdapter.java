package org.wt.example1;

import org.objectweb.asm.*;

/**
 * bytecode utils  https://blog.51cto.com/7317859/2106469
 *ASM介绍及简易教程 https://blog.csdn.net/wodeyuer125/article/details/44618679
 * 关于java字节码框架ASM的学习 https://www.cnblogs.com/liuling/archive/2013/05/25/asm.html
 *Type https://www.jianshu.com/p/8c5a179fda58
 */
/*
public class AddTimeClassAdapter extends ClassAdapter {
    private String owner;
    private boolean isInterface;

    public AddTimeClassAdapter(ClassVisitor cv) {
        super(cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        cv.visit(version, access, name, signature, superName, interfaces);
        owner = name;
        isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {

        //invoke my static method
        //add copied method
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (!name.equals("<init>") && !isInterface && mv != null) {
            //为方法添加计时功能
            mv = new AddTimeMethodAdapter(mv);
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        //添加字段
        cv.visitEnd();
    }

    class AddTimeMethodAdapter extends MethodAdapter {
        public AddTimeMethodAdapter(MethodVisitor mv) {
            super(mv);
        }

        @Override
        public void visitCode() {
            //invoke org.wt.MyInvoker#ivk ,the return type is void.
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,"org/wt/"+MyInvoker.example()+"/MyInvoker","ivk","()V");
            //mv.visitMethodInsn(Opcodes.INVOKESTATIC,"org/wt/MyInvoker","ivk","Ljava.lang.Object;");
           // mv.visitInsn(Opcodes.LSUB);
            mv.visitCode();
            //mv.visitFieldInsn(Opcodes.GETSTATIC, owner, "timer", "J");
            //mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J");
            //mv.visitInsn(Opcodes.LSUB);
            //mv.visitFieldInsn(Opcodes.PUTSTATIC, owner, "timer", "J");
        }

        @Override
        public void visitInsn(int opcode) {
            mv.visitInsn(opcode);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocal) {
            //mv.visitMaxs(maxStack + 4, maxLocal);
            mv.visitMaxs(maxStack , maxLocal);
        }
    }

}*/
