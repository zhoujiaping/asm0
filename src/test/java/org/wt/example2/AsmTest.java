package org.wt.example2;

import groovy.lang.GroovyClassLoader;
import org.junit.Test;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.util.ASMifier;

import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * https://www.jianshu.com/p/905be2a9a700
 * invokeDynamic https://www.cnblogs.com/sheeva/p/6344388.html
 * https://www.jianshu.com/p/12d5ca2bb2a8
 */
public class AsmTest {

    @Test
    public void testASMifier() throws Exception {
        ASMifier.main(new String[]{Demo.class.getName()});
    }

    @Test
    public void testCopyMethod() throws Exception {
        String className = "org.wt.example2.Demo";
        try {
            InputStream in = new FileInputStream(new File("/home/wt/IdeaProjects/cglibtest/target/test-classes/org/wt/example2/Demo.class"));

            ClassReader reader = new ClassReader(in);                              // 1. 创建 ClassReader 读入 .class 文件到内存中
            ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);                 // 2. 创建 ClassWriter 对象，将操作之后的字节码的字节数组回写
            ClassVisitor change = new ChangeVisitor(writer);                                        // 3. 创建自定义的 ClassVisitor 对象
            reader.accept(change, ClassReader.EXPAND_FRAMES);
            // 4. 将 ClassVisitor 对象传入 ClassReader 中

            MyInvoker.map.put(className,new MyInvoker(className){
                @Override
                public Object invoke(Object target, String method, String[] pts,Object... args) throws Throwable {
                    System.out.println("invoke");
                    //return super.invoke(target, method, args);
                    return null;
                }
            });
            byte[] code = writer.toByteArray();                                                               // 获取修改后的 class 文件对应的字节数组
            try {
                String filename = System.getProperty("user.dir") + "/src/test/resources/"+Helper.example()+"/org/wt/"+ Helper.example()+"/Demo.class";
                new File(filename).getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(filename);    // 将二进制流写到本地磁盘上
                fos.write(code);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            GroovyClassLoader cl = new GroovyClassLoader();
            Class clazz = cl.defineClass(className,code);
            //clazz = cl.loadClass(className);
            Object personObj = clazz.newInstance();
            Method nameMethod = clazz.getDeclaredMethod("hello1", String.class,int.class,clazz);
            nameMethod.setAccessible(true);
            nameMethod.invoke(personObj, "",10,personObj);
            MyInvoker.map.put(className,new MyInvoker(className){
                @Override
                public Object invoke(Object target, String method,String[] pts, Object[] args) throws Throwable {
                    System.out.println("invoke");
                    Class c = cl.loadClass(className);
                    Method[] ms = c.getDeclaredMethods();
                    Method proceed = c.getDeclaredMethod(method+"_ivk_pxy",String.class,int.class,clazz);
                    if(!proceed.isAccessible()){
                        proceed.setAccessible(true);
                    }
                    return proceed.invoke(target,args);
                }
            });
            nameMethod.invoke(personObj, "",10,personObj);
            System.out.println("Success!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failure!");
        }

    }
    //implements Opcodes,so that we can use Constant is Opcodes conveniently
    static class ChangeVisitor extends ClassVisitor implements Opcodes {
        String clazzname0;
        String clazzname;
        int isPxy;

        ChangeVisitor(ClassVisitor classVisitor) {
            super(Opcodes.ASM5, classVisitor);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            if (name.equals("_pxy")) {
                isPxy = 1;
            }
            return super.visitField(access, name, desc, signature, value);
        }

        @Override
        public void visitEnd() {
            if (isPxy == 0) {
                // 添加字段：public static List _my_instances;
                super.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, "_pxy",
                        "I", null, 0);
            }
            super.visitEnd();
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            //is interface
            if ((access & ACC_INTERFACE) != 0) {
                return;
            }
            //is enum
            if ((access & ACC_ENUM) != 0) {
                return;
            }
            clazzname0 = name;
            clazzname = name.replaceAll("/", ".");
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

            MethodVisitor mv = null;
            if (name.equals("<init>")) {
                mv = super.visitMethod(access, name, desc, signature, exceptions);
                return mv;
            }
            if (isPxy == 1) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
            if (name.endsWith("'lambda$'")) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
            if ((access & ACC_ABSTRACT) != 0) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
//add a method
            /*mv = super.visitMethod(access, name, desc, signature, exceptions);
            interceptMethod2(access, name, desc, signature, exceptions);*/
//rename method, change to private, and add time count code
            mv = super.visitMethod(ACC_PRIVATE, name + "_ivk_pxy", desc, signature, exceptions);
            interceptMethod(access, name, desc, signature, exceptions);
            return mv;
        }

        private void interceptMethod(int access, String name, String desc, String signature, String[] exceptions) {

            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            mv.visitCode();
            //获取静态属性 放入栈。操作符GETSTATIC 从栈中弹出0个值，将结果入栈。slot= slot-0+1（初始slot=0）
            //TODO
            mv.visitFieldInsn(GETSTATIC, "org/wt/example2/MyInvoker", "map", "Ljava/util/Map;");
            //获取常量（字符串，也可以其是某些其他类型） 放入栈。从栈中弹出1个值，将结果入栈。slot=slot-0+1(long、dubbo的slot为2，其他为1)
            mv.visitLdcInsn(Type.getType("L" + clazzname0 + ";"));
            //执行实例方法 结果入栈。slot=slot-1+1
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
            //execute map#get  invoker。slot=slot-1+1
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
            //check the type of return value(map#get)。slot=slot-1+1
            //TODO
            mv.visitTypeInsn(CHECKCAST, "org/wt/example2/MyInvoker");
            //put this in stack。slot=slot+1
            mv.visitVarInsn(ALOAD, 0);
            //put methodName in stack。slot=slot+1
            mv.visitLdcInsn(name);
            Type[] argumentTypes = Type.getArgumentTypes(desc);
            //slot=slot+1
            mv.visitIntInsn(Opcodes.BIPUSH, argumentTypes.length);
            //put array in stack。slot=slot-1+1
            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
            for (int i = 0; i < argumentTypes.length; i++) {
                Type argumentType = argumentTypes[i];
                //slot=slot+1
                mv.visitInsn(DUP);
                //slot=slot+1
                mv.visitIntInsn(Opcodes.BIPUSH, i);
                //put constant valur to stack。slot=slot+1
                //argumentType.getClassName() int.class=>int
                //argumentType.toString() String.class=>Ljava/lang/String; int.class=>I
                //TODO
                mv.visitLdcInsn(argumentType.getClassName());
                //get variable from stack,then put to array
                //slot=slot-3+1
                mv.visitInsn(AASTORE);
            }
            //slot=slot+1
            mv.visitIntInsn(Opcodes.BIPUSH, argumentTypes.length);
            //put array in stack
            //slot=slot-1+1
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            // 对每一个参数，都将对应局部变量表的位置入栈
            // 计算局部变量表的位置，其中 double 和 long 占用两个槽，其他占用一个槽
            int offset = 1;
            for (int i = 0; i < argumentTypes.length; i++) {
                Type argumentType = argumentTypes[i];
                if (argumentType.equals(Type.BYTE_TYPE)){
                    //slot=slot+1
                    mv.visitInsn(DUP);
                    //slot=slot+1
                    mv.visitIntInsn(Opcodes.BIPUSH, i);
                    //slot=slot+1
                    mv.visitVarInsn(Opcodes.ILOAD, offset);
                    //slot=slot-1+1
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Byte.class),
                            "valueOf", "(B)Ljava/lang/Byte;", false);
                    //slot=slot-3+1
                    mv.visitInsn(AASTORE);
                    offset = offset + 1;
                    //load variable(position is i+N) from local var table
                }else if(argumentType.equals(Type.BOOLEAN_TYPE)){
                    mv.visitInsn(DUP);
                    mv.visitIntInsn(Opcodes.BIPUSH, i);
                    mv.visitVarInsn(Opcodes.ILOAD, offset);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Boolean.class),
                            "valueOf", "(Z)Ljava/lang/Boolean;", false);
                    mv.visitInsn(AASTORE);
                    offset = offset + 1;
                }else if(argumentType.equals(Type.CHAR_TYPE)) {
                    mv.visitInsn(DUP);
                    mv.visitIntInsn(Opcodes.BIPUSH, i);
                    mv.visitVarInsn(Opcodes.ILOAD, offset);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Character.class),
                            "valueOf", "(C)Ljava/lang/Character;", false);
                    mv.visitInsn(AASTORE);
                    offset = offset + 1;
                }else if( argumentType.equals(Type.SHORT_TYPE)){
                    mv.visitInsn(DUP);
                    mv.visitIntInsn(Opcodes.BIPUSH, i);
                    mv.visitVarInsn(Opcodes.ILOAD, offset);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Short.class),
                            "valueOf", "(S)Ljava/lang/Short;", false);
                    mv.visitInsn(AASTORE);
                    offset = offset + 1;
                }else if(argumentType.equals(Type.INT_TYPE)) {
                    mv.visitInsn(DUP);
                    mv.visitIntInsn(Opcodes.BIPUSH, i);
                    mv.visitVarInsn(Opcodes.ILOAD, offset);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Integer.class),
                            "valueOf", "(I)Ljava/lang/Integer;", false);
                    mv.visitInsn(AASTORE);
                    offset = offset + 1;
                } else if (argumentType.equals(Type.LONG_TYPE)) {
                    mv.visitInsn(DUP);
                    mv.visitIntInsn(Opcodes.BIPUSH, i);

                    mv.visitVarInsn(Opcodes.LLOAD, offset);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Long.class),
                            "valueOf", "(J)Ljava/lang/Long;", false);
                    mv.visitInsn(AASTORE);
                    offset = offset + 2;
                } else if (argumentType.equals(Type.FLOAT_TYPE)) {
                    mv.visitInsn(DUP);
                    mv.visitIntInsn(Opcodes.BIPUSH, i);

                    mv.visitVarInsn(Opcodes.FLOAD, offset);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Float.class),
                            "valueOf", "(F)Ljava/lang/Float;", false);
                    mv.visitInsn(AASTORE);
                    offset = offset + 1;
                } else if (argumentType.equals(Type.DOUBLE_TYPE)) {
                    mv.visitInsn(DUP);
                    mv.visitIntInsn(Opcodes.BIPUSH, i);

                    mv.visitVarInsn(Opcodes.DLOAD, offset);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Double.class),
                            "valueOf", "(D)Ljava/lang/Double;", false);
                    mv.visitInsn(AASTORE);
                    offset = offset + 1;
                } else if(argumentType.equals(Type.VOID)){
                } else {
                    mv.visitInsn(DUP);
                    mv.visitIntInsn(Opcodes.BIPUSH, i);
                    mv.visitVarInsn(Opcodes.ALOAD,offset);
                    mv.visitInsn(AASTORE);
                    offset = offset + 1;
                }
            }
            //TODO
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/wt/example2/MyInvoker", "invoke", "(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;", false);

            Type retType = Type.getReturnType(desc);
            int opcode;
            if (retType.equals(Type.BYTE_TYPE)
                    || retType.equals(Type.BOOLEAN_TYPE)
                    || retType.equals(Type.CHAR_TYPE)
                    || retType.equals(Type.SHORT_TYPE)
                    || retType.equals(Type.INT_TYPE)) {
                opcode = IRETURN;
            } else if (retType.equals(Type.LONG_TYPE)) {
                opcode = LRETURN;
            } else if (retType.equals(Type.FLOAT_TYPE)) {
                opcode = FRETURN;
            } else if (retType.equals(Type.DOUBLE_TYPE)) {
                opcode = DRETURN;
            } else if (retType.equals(Type.VOID_TYPE)) {
                opcode = RETURN;
            } else {
                opcode = ARETURN;
            }
            //check return type
            //TODO
            mv.visitTypeInsn(CHECKCAST, "java/util/List");
            //return
            mv.visitInsn(opcode);
            //maxStack= 栈slot峰值，maxocals= 本地变量表slot峰值。因为设置了COMPUTE_MAXS自动计算，所以这里随便填，但不能不调用。
            mv.visitMaxs(1, 1);
            mv.visitEnd();

        }
    }
}
