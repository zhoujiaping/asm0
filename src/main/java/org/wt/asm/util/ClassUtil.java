package org.wt.asm.util;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * java的类型，有内部名称（icn），描述（desc），类型名（tcn/ctn），类名（cn）。
 * Class的api时有问题的，它的forName方法不能获取基本类型的类。
 *
 * class:类。
 * 基本类型，例如 boolean, byte, short, char, int, long, float, double
 * 基本类型数组，例如 class [Z, [B, [S, [C, [I, [J, [F, [D
 * 引用类型，例如class java.lang.String,class java.lang.Object
 * 引用类型数组，例如 class [Ljava.lang.String;
 *
 * tcn(ctn):类型名。Class#typeName,或者Type#className。两者结果是一样的。
 * 基本类型，例如 boolean, byte
 * 基本类型数组,例如 boolean[], byte[]
 * 引用类型，例如 java.lang.String
 * 引用类型数组，例如 java.lang.String[]
 *
 * cn:类名 class name。
 * 基本类型，例如 boolean, byte
 * 基本类型数组,例如 [Z, [B
 * 引用类型，例如 java.lang.String
 * 引用类型数组，例如 [Ljava.lang.String;
 *
 * icn:内部类名,internal class name。原始类型没有内部类名。但是为了方便，我们约定原始类型有内部类名，统称为picn。
 *  picn，例如 Z,B,S,C,I,L,F,D
 * 原始类型数组，例如 [Z,[B
 * 引用类型，例如 java/lang/String
 * 引用类型数组，例如 [Ljava/lang/String;
 *
 * desc:类描述符。
 * 基本类型，例如Z,B,S,C,I,J,F,D
 * 基本类型的数组，例如[Z,[B,[S,[C,[I,[J,[F,[D
 * 引用类型，例如Ljava/lang/String;注意后面的分号不能省略。
 * 引用类型数组，例如 [Ljava/lang/String;
 *
 * owner:用指令执行方法时，方法的所有者。比如String#valueOf的owner为java/lang/String。
 *
 * 基于以上分析，整个类型处理以cn为核心进行设计最合理。
 *
 * @author wt
 */
public abstract class ClassUtil {
    private static Map<Type, Integer> pt2retOpCode = new HashMap<>();

    private static Map<String, String> cn2picn = new HashMap<>();
    private static Map<String, String> picn2cn;

    private static Map<String, Class> pcn2c = new HashMap<>();

    static {
        initPt2retOpCode();
        initCn2picn();
        picn2cn = CollectionUtil.reverseKeyValue(cn2picn);
        initPicn2c();
    }

    private static void initCn2picn() {
        cn2picn.put("boolean", "Z");
        cn2picn.put("byte", "B");
        cn2picn.put("char", "C");
        cn2picn.put("short", "S");
        cn2picn.put("int", "I");
        cn2picn.put("long", "J");
        cn2picn.put("float", "F");
        cn2picn.put("double", "D");
    }

    private static void initPt2retOpCode() {
        pt2retOpCode.put(Type.BOOLEAN_TYPE, Opcodes.IRETURN);
        pt2retOpCode.put(Type.BYTE_TYPE, Opcodes.IRETURN);
        pt2retOpCode.put(Type.CHAR_TYPE, Opcodes.IRETURN);
        pt2retOpCode.put(Type.SHORT_TYPE, Opcodes.IRETURN);
        pt2retOpCode.put(Type.INT_TYPE, Opcodes.IRETURN);
        pt2retOpCode.put(Type.LONG_TYPE, Opcodes.LRETURN);
        pt2retOpCode.put(Type.FLOAT_TYPE, Opcodes.FRETURN);
        pt2retOpCode.put(Type.DOUBLE_TYPE, Opcodes.DRETURN);
        pt2retOpCode.put(Type.VOID_TYPE, Opcodes.RETURN);
    }

    private static void initPicn2c() {
        pcn2c.put("boolean", boolean.class);
        pcn2c.put("byte", byte.class);
        pcn2c.put("char", char.class);
        pcn2c.put("short", short.class);
        pcn2c.put("int", int.class);
        pcn2c.put("long", long.class);
        pcn2c.put("float", float.class);
        pcn2c.put("double", double.class);
    }

    public static int typeToRetOpcode(Type type) {
        Integer opcode = pt2retOpCode.get(type);
        if (opcode == null) {
            return Opcodes.ARETURN;
        }
        return opcode;
    }

    /**
     * 能够和任何Class#getName()进行互逆操作
     */
    public static Class<?> forName(String cn,boolean init,ClassLoader cl) throws ClassNotFoundException {
        return cnToClass(cn,init,cl);
    }
    public static Class<?> forName(String cn) throws ClassNotFoundException {
        return cnToClass(cn);
    }

    public static Class cnToClass(String cn,boolean init,ClassLoader cl) throws ClassNotFoundException {
        Class c = pcn2c.get(cn);
        if (c != null) {
            return c;
        }
        return Class.forName(cn,init,cl);
    }
    public static Class cnToClass(String cn) throws ClassNotFoundException {
        Class c = pcn2c.get(cn);
        if (c != null) {
            return c;
        }
        return Class.forName(cn);
    }

    public static String classToCn(Class c) {
        return c.getName();
    }

    public static String cnToCtn(String cn) {
        //分两种情况。非数组和数组，需要处理数组。
        int i = cn.lastIndexOf("[");
        if (i < 0) {
            return cn;
        }
        String part1 = cn.substring(0, i + 1);
        String part2 = cn.substring(i + 1);
        String sq = "";
        for (int j = 0; j < part1.length(); j++) {
            sq += "[]";
        }
        return part2 + sq;
    }

    public static String ctnToCn(String ctn) {
        //分两种情况。非数组和数组。要处理非数组。
        //数组又分 原始类型、引用类型两种情况。
        int sqi = ctn.indexOf("[");
        if (sqi < 0) {
            return ctn;
        }
        String part1 = ctn.substring(0, sqi);
        String part2 = ctn.substring(sqi);
        String cn = cn2picn.get(part1);
        if(cn != null){
            return part2.replaceAll("]", "")+cn;
        }
        return part2.replaceAll("]", "")+"L"+part1+";";
    }

    public static String cnToDesc(String cn) {
        //分两种情况。非数组和数组。要处理非数组。
        if (cn.startsWith("[")) {
            return cn.replaceAll("\\.", "/");
        }
        //非数组又分 原始类型、引用类型两种情况。
        String picn = cn2picn.get(cn);
        if (picn != null) {
            return picn;
        }
        return "L" + cn.replaceAll("\\.", "/") + ";";
    }
    public static String descToCn(String desc){
        //分两种情况。非数组和数组。要处理非数组。
        if (desc.startsWith("[")) {
            return desc.replaceAll("/", ".");
        }
        //非数组又分 原始类型、引用类型两种情况。
        String cn = picn2cn.get(desc);
        if(cn != null){
            return cn;
        }
        return cn.substring(1,cn.length()-1).replaceAll("/",".");
    }

    public static String cnToIcn(String cn) {
        //分两种情况。非数组和数组。要处理非数组。
        if (cn.startsWith("[")) {
            return cn.replaceAll("\\.", "/");
        }
        //非数组又分 原始类型、引用类型两种情况。
        String picn = cn2picn.get(cn);
        if (picn != null) {
            return picn;
        }
        return cn.replaceAll("\\.", "/");
    }
    public static String icnToCn(String icn){
        //分两种情况。非数组和数组。要处理非数组。
        if(icn.startsWith("[")){
            return icn;
        }
        String cn = picn2cn.get(icn);
        if(cn!=null){
            return cn;
        }
        return cn.replaceAll("/",".");
    }
}
