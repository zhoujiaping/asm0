package org.wt.asm.aop;

import org.objectweb.asm.*;
import org.wt.asm.util.ClassUtil;

//implements Opcodes,so that we can use Constant is Opcodes conveniently
public class ClassAopVisitor extends ClassVisitor implements Opcodes {

    private String internalClassname;
    private String classname;
    //是否被转换过
    private boolean trans;
    //是否为interface
    private boolean itf;
    //是否为enum
    private boolean enm;

    public ClassAopVisitor(ClassVisitor classVisitor) {
        super(ASM5, classVisitor);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        //如果类已经有 _pxy字段，则认为已经被转换过。无需再次转换。
        if (name.equals(MethodAop.SUFFIX)) {
            trans = true;
        }
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public void visitEnd() {
        //转换完毕，添加标记字段，表示该类是已经转换过的类。
        if (!trans) {
            // 添加字段：private static int _pxy = 0;
            super.visitField(ACC_PRIVATE | ACC_STATIC, MethodAop.SUFFIX,
                    "I", null, 0);
        }
        super.visitEnd();
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        //is interface 如果是接口，则不转换。
        if ((access & ACC_INTERFACE) != 0) {
            itf = true;
            return;
        }
        //is enum
        if ((access & ACC_ENUM) != 0) {
            enm = true;
            return;
        }
        internalClassname = name;
        classname = name.replaceAll("/", ".");
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        MethodVisitor mv = null;
        if (name.equals("<init>")) {
            mv = super.visitMethod(access, name, desc, signature, exceptions);
            return mv;
        }
        if (trans) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
        //如果时lambada表达式，就不对该方法做aop处理
        if (name.endsWith("'lambda$'")) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
        //抽象方法，不做aop处理
        if ((access & ACC_ABSTRACT) != 0) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
        //rename method, change to private, and add time count code
        //修改方法名称，改为私有方法，加个后缀。aop时如果需要调用原有逻辑，就通过调用该方法实现。
        mv = super.visitMethod(ACC_PRIVATE, name + MethodAop.SUFFIX, desc, signature, exceptions);
        //add a method. 原有的方法，修改方法体，调用代理方法。
        interceptMethod(access, name, desc, signature, exceptions);
        return mv;
    }

    private void interceptMethod(int access, String name, String desc, String signature, String[] exceptions) {

        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        mv.visitCode();
        //开始生成方法体
        //获取静态属性 放入栈。操作符GETSTATIC 从栈中弹出0个值，将结果入栈。slot= slot-0+1（初始slot=0）
        //TODO
        mv.visitFieldInsn(GETSTATIC, "org/wt/example2/MyInvoker", "map", "Ljava/util/Map;");
        //获取常量（字符串，也可以其是某些其他类型） 放入栈。从栈中弹出1个值，将结果入栈。slot=slot-0+1(long、dubbo的slot为2，其他为1)
        mv.visitLdcInsn(Type.getType("L" + internalClassname + ";"));
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
        mv.visitIntInsn(BIPUSH, argumentTypes.length);
        //put array in stack。slot=slot-1+1
        mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
        for (int i = 0; i < argumentTypes.length; i++) {
            Type argumentType = argumentTypes[i];
            //slot=slot+1
            mv.visitInsn(DUP);
            //slot=slot+1
            mv.visitIntInsn(BIPUSH, i);
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
        mv.visitIntInsn(BIPUSH, argumentTypes.length);
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
                mv.visitIntInsn(BIPUSH, i);
                //slot=slot+1
                mv.visitVarInsn(ILOAD, offset);
                //slot=slot-1+1
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Byte.class),
                        "valueOf", "(B)Ljava/lang/Byte;", false);
                //slot=slot-3+1
                mv.visitInsn(AASTORE);
                offset = offset + 1;
                //load variable(position is i+N) from local var table
            }else if(argumentType.equals(Type.BOOLEAN_TYPE)){
                mv.visitInsn(DUP);
                mv.visitIntInsn(BIPUSH, i);
                mv.visitVarInsn(ILOAD, offset);
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Boolean.class),
                        "valueOf", "(Z)Ljava/lang/Boolean;", false);
                mv.visitInsn(AASTORE);
                offset = offset + 1;
            }else if(argumentType.equals(Type.CHAR_TYPE)) {
                mv.visitInsn(DUP);
                mv.visitIntInsn(BIPUSH, i);
                mv.visitVarInsn(ILOAD, offset);
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Character.class),
                        "valueOf", "(C)Ljava/lang/Character;", false);
                mv.visitInsn(AASTORE);
                offset = offset + 1;
            }else if( argumentType.equals(Type.SHORT_TYPE)){
                mv.visitInsn(DUP);
                mv.visitIntInsn(BIPUSH, i);
                mv.visitVarInsn(ILOAD, offset);
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Short.class),
                        "valueOf", "(S)Ljava/lang/Short;", false);
                mv.visitInsn(AASTORE);
                offset = offset + 1;
            }else if(argumentType.equals(Type.INT_TYPE)) {
                mv.visitInsn(DUP);
                mv.visitIntInsn(BIPUSH, i);
                mv.visitVarInsn(ILOAD, offset);
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Integer.class),
                        "valueOf", "(I)Ljava/lang/Integer;", false);
                mv.visitInsn(AASTORE);
                offset = offset + 1;
            } else if (argumentType.equals(Type.LONG_TYPE)) {
                mv.visitInsn(DUP);
                mv.visitIntInsn(BIPUSH, i);

                mv.visitVarInsn(LLOAD, offset);
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Long.class),
                        "valueOf", "(J)Ljava/lang/Long;", false);
                mv.visitInsn(AASTORE);
                offset = offset + 2;
            } else if (argumentType.equals(Type.FLOAT_TYPE)) {
                mv.visitInsn(DUP);
                mv.visitIntInsn(BIPUSH, i);

                mv.visitVarInsn(FLOAD, offset);
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Float.class),
                        "valueOf", "(F)Ljava/lang/Float;", false);
                mv.visitInsn(AASTORE);
                offset = offset + 1;
            } else if (argumentType.equals(Type.DOUBLE_TYPE)) {
                mv.visitInsn(DUP);
                mv.visitIntInsn(BIPUSH, i);

                mv.visitVarInsn(DLOAD, offset);
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Double.class),
                        "valueOf", "(D)Ljava/lang/Double;", false);
                mv.visitInsn(AASTORE);
                offset = offset + 1;
            } else if(argumentType.equals(Type.VOID)){
            } else {
                mv.visitInsn(DUP);
                mv.visitIntInsn(BIPUSH, i);
                mv.visitVarInsn(ALOAD,offset);
                mv.visitInsn(AASTORE);
                offset = offset + 1;
            }
        }
        //TODO
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/wt/example2/MyInvoker", "invoke", "(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;", false);

        Type retType = Type.getReturnType(desc);
        int opcode = ClassUtil.typeToRetOpcode(retType);
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