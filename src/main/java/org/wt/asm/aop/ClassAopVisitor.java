package org.wt.asm.aop;

import org.objectweb.asm.*;
import org.wt.asm.util.ClassUtil;

//implements Opcodes,so that we can use Constant is Opcodes conveniently
public class ClassAopVisitor extends ClassVisitor implements Opcodes {

	private String classname;
	// 是否被转换过
	private boolean trans;
	// 是否为interface
	private boolean itf;
	// 是否为enum
	private boolean enm;
	/**
	 * compute maxStack,maxLocals to improve perform
	 */
	private int stack;
	private int maxStack;
	private int locals;
	private int maxLocals;

	private void reset(){
		stack=maxStack=locals=maxLocals=0;
	}
	private void computeStack(int minus,int plus){
		stack+=plus-minus;
		maxStack = maxStack<stack?stack:maxStack;
	}
	private void computeLocals(int minus,int plus){
		locals+=plus-minus;
		maxLocals = maxLocals<locals?locals:maxLocals;
	}

	ClassAopVisitor(ClassVisitor classVisitor) {
		super(ASM5, classVisitor);
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		// 如果类已经有 _pxy字段，则认为已经被转换过。无需再次转换。
		if (name.equals(MethodAop.SUFFIX)) {
			trans = true;
		}
		return super.visitField(access, name, desc, signature, value);
	}

	@Override
	public void visitEnd() {
		if (itf || enm) {
			return;
		}
		// 转换完毕，添加标记字段，表示该类是已经转换过的类。
		if (!trans) {
			// 添加字段：private static int _pxy = 0;
			super.visitField(ACC_PRIVATE | ACC_STATIC, MethodAop.SUFFIX, "I", null, 0);
		}
		super.visitEnd();
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		// is interface 如果是接口，则不转换。
		if ((access & ACC_INTERFACE) != 0) {
			itf = true;
			return;
		}
		// is enum
		if ((access & ACC_ENUM) != 0) {
			enm = true;
			return;
		}
		classname = name.replaceAll("/", ".");
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

		MethodVisitor mv;
		if (name.equals("<init>")) {
			mv = super.visitMethod(access, name, desc, signature, exceptions);
			return mv;
		}
		if (trans || itf || enm) {
			return super.visitMethod(access, name, desc, signature, exceptions);
		}
		// 如果时lambada表达式，就不对该方法做aop处理
		if (name.endsWith("lambda$")) {
			return super.visitMethod(access, name, desc, signature, exceptions);
		}
		// 抽象方法，不做aop处理
		if ((access & ACC_ABSTRACT) != 0) {
			return super.visitMethod(access, name, desc, signature, exceptions);
		}
		// rename method, change to private, and add time count code
		// 修改方法名称，改为私有方法，加个后缀。aop时如果需要调用原有逻辑，就通过调用该方法实现。
		mv = super.visitMethod((access&~(ACC_PUBLIC|ACC_PROTECTED))|ACC_PRIVATE, name + MethodAop.SUFFIX, desc, signature, exceptions);
		// add a method. 原有的方法，修改方法体，调用代理方法。
		interceptMethod(access, name, desc, signature, exceptions);
		return mv;
	}

	private MethodVisitor interceptMethod(int access, String name, String desc, String signature, String[] exceptions) {

		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		mv.visitCode();
		reset();
		boolean isStatic = (access & ACC_STATIC) != 0;
		// 开始生成方法体
		// 获取静态属性 放入栈。操作符GETSTATIC 从栈中弹出0个值，将结果入栈。slot= slot-0+1（初始slot=0）
		mv.visitFieldInsn(GETSTATIC, MethodAop.ICN, MethodAop.MAP_NAME, MethodAop.MAP_DESC);
		computeStack(0,1);
		// 获取常量（字符串，也可以其是某些其他类型）
		// 放入栈。从栈中弹出1个值，将结果入栈。slot=slot-0+1(long、dubbo的slot为2，其他为1)
		mv.visitLdcInsn(Type.getType(ClassUtil.cnToDesc(classname)));
		computeStack(0,1);
		// 执行实例方法 结果入栈。slot=slot-1+1
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
		computeStack(0,1);
		// execute map#get invoker。slot=slot-1+1
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
		computeStack(1,1);
		// check the type of return value(map#get)。slot=slot-1+1
		mv.visitTypeInsn(CHECKCAST, MethodAop.ICN);
		computeStack(1,1);
		// put this in stack。slot=slot+1
		if(isStatic){
			mv.visitInsn(ACONST_NULL);
		}else{
			mv.visitVarInsn(ALOAD, 0);
		}
		computeStack(0,1);
		// put methodName in stack。slot=slot+1
		mv.visitLdcInsn(name);
		computeStack(0,1);
		Type[] argumentTypes = Type.getArgumentTypes(desc);
		int varSlots = argumentTypes.length;

				// slot=slot+1
		mv.visitIntInsn(BIPUSH, argumentTypes.length);
		computeStack(0,1);
		// put array in stack。slot=slot-1+1
		mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
		computeStack(1,1);
		for (int i = 0; i < argumentTypes.length; i++) {
			Type argumentType = argumentTypes[i];
			// slot=slot+1
			mv.visitInsn(DUP);
			computeStack(0,1);
			// slot=slot+1
			mv.visitIntInsn(BIPUSH, i);
			computeStack(0,1);
			// put constant value to stack。slot=slot+1
			mv.visitLdcInsn(ClassUtil.ctnToCn(argumentType.getClassName()));
			computeStack(0,1);
			// get variable from stack,then put to array
			// slot=slot-3+1
			mv.visitInsn(AASTORE);
			computeStack(3,1);
		}
		computeLocals(0,argumentTypes.length);
		// slot=slot+1
		mv.visitIntInsn(BIPUSH, argumentTypes.length);
		computeStack(0,1);
		// put array in stack
		// slot=slot-1+1
		mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
		computeStack(0,1);
		// 对每一个参数，都将对应局部变量表的位置入栈
		// 计算局部变量表的位置，其中 double 和 long 占用两个槽，其他占用一个槽
		int offset = isStatic?0:1;
		for (int i = 0; i < argumentTypes.length; i++) {
			Type argumentType = argumentTypes[i];
			if (argumentType.equals(Type.BYTE_TYPE)) {
				// slot=slot+1
				mv.visitInsn(DUP);
				computeStack(0,1);
				// slot=slot+1
				mv.visitIntInsn(BIPUSH, i);
				computeStack(0,1);
				// slot=slot+1
				mv.visitVarInsn(ILOAD, offset);
				computeStack(0,1);
				// slot=slot-1+1
				mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Byte.class), "valueOf", "(B)Ljava/lang/Byte;",
						false);
				computeStack(1,1);
				// slot=slot-3+1
				mv.visitInsn(AASTORE);
				computeStack(3,1);
				offset = offset + 1;
				// load variable(position is i+N) from local var table
			} else if (argumentType.equals(Type.BOOLEAN_TYPE)) {
				mv.visitInsn(DUP);
				computeStack(0,1);
				mv.visitIntInsn(BIPUSH, i);
				computeStack(0,1);
				mv.visitVarInsn(ILOAD, offset);
				computeStack(0,1);
				mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Boolean.class), "valueOf",
						"(Z)Ljava/lang/Boolean;", false);
				computeStack(1,1);
				mv.visitInsn(AASTORE);
				computeStack(3,1);
				offset = offset + 1;
			} else if (argumentType.equals(Type.CHAR_TYPE)) {
				mv.visitInsn(DUP);
				computeStack(0,1);
				mv.visitIntInsn(BIPUSH, i);
				computeStack(0,1);
				mv.visitVarInsn(ILOAD, offset);
				computeStack(0,1);
				mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Character.class), "valueOf",
						"(C)Ljava/lang/Character;", false);
				computeStack(1,1);
				mv.visitInsn(AASTORE);
				computeStack(3,1);
				offset = offset + 1;
			} else if (argumentType.equals(Type.SHORT_TYPE)) {
				mv.visitInsn(DUP);
				computeStack(0,1);
				mv.visitIntInsn(BIPUSH, i);
				computeStack(0,1);
				mv.visitVarInsn(ILOAD, offset);
				computeStack(0,1);
				mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Short.class), "valueOf", "(S)Ljava/lang/Short;",
						false);
				computeStack(1,1);
				mv.visitInsn(AASTORE);
				computeStack(3,1);
				offset = offset + 1;
			} else if (argumentType.equals(Type.INT_TYPE)) {
				mv.visitInsn(DUP);
				computeStack(0,1);
				mv.visitIntInsn(BIPUSH, i);
				computeStack(0,1);
				mv.visitVarInsn(ILOAD, offset);
				computeStack(0,1);
				mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Integer.class), "valueOf",
						"(I)Ljava/lang/Integer;", false);
				computeStack(1,1);
				mv.visitInsn(AASTORE);
				computeStack(3,1);
				offset = offset + 1;
			} else if (argumentType.equals(Type.LONG_TYPE)) {
				mv.visitInsn(DUP);
				computeStack(0,1);
				mv.visitIntInsn(BIPUSH, i);
				computeStack(0,1);
				mv.visitVarInsn(LLOAD, offset);
				computeStack(0,2);
				mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Long.class), "valueOf", "(J)Ljava/lang/Long;",
						false);
				computeStack(2,1);
				mv.visitInsn(AASTORE);
				computeStack(3,1);
				offset = offset + 2;
			} else if (argumentType.equals(Type.FLOAT_TYPE)) {
				mv.visitInsn(DUP);
				computeStack(0,1);
				mv.visitIntInsn(BIPUSH, i);
				computeStack(0,1);

				mv.visitVarInsn(FLOAD, offset);
				computeStack(0,1);
				mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Float.class), "valueOf", "(F)Ljava/lang/Float;",
						false);
				computeStack(1,1);
				mv.visitInsn(AASTORE);
				computeStack(3,1);
				offset = offset + 1;
			} else if (argumentType.equals(Type.DOUBLE_TYPE)) {
				mv.visitInsn(DUP);
				computeStack(0,1);
				mv.visitIntInsn(BIPUSH, i);
				computeStack(0,1);

				mv.visitVarInsn(DLOAD, offset);
				computeStack(0,2);
				mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Double.class), "valueOf", "(D)Ljava/lang/Double;",
						false);
				computeStack(2,2);
				mv.visitInsn(AASTORE);
				computeStack(3,1);
				offset = offset + 1;
			} else if (argumentType.equals(Type.VOID)) {
			} else {
				mv.visitInsn(DUP);
				computeStack(0,1);
				mv.visitIntInsn(BIPUSH, i);
				computeStack(0,1);
				mv.visitVarInsn(ALOAD, offset);
				computeStack(1,1);
				mv.visitInsn(AASTORE);
				computeStack(3,1);
				offset = offset + 1;
			}
		}
		mv.visitMethodInsn(INVOKEVIRTUAL, MethodAop.ICN, MethodAop.INVOKE_METHOD,
				"(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
				false);
		computeStack(stack,1);
		Type retType = Type.getReturnType(desc);
		int opcode = ClassUtil.typeToRetOpcode(retType);
		// check return type
		mv.visitTypeInsn(CHECKCAST, ClassUtil.cnToIcn(retType.getClassName()));
		computeStack(1,1);
		// return
		mv.visitInsn(opcode);
		computeStack(1,0);
		if(stack!=0){
			System.err.println("compute stack error,stack= "+stack);
		}
		// maxStack= 栈slot峰值，maxocals=
		// 本地变量表slot峰值。因为设置了COMPUTE_MAXS自动计算，所以这里随便填，但不能不调用。
		mv.visitMaxs(maxStack, maxLocals);
		mv.visitEnd();
		return mv;
	}
}