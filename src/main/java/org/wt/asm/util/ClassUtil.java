package org.wt.asm.util;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

/**
 * java的类型，有内部名称（internalClassname），描述（desc），类型名（typename），类名（classname）。
 * Class的api时有问题的，对于基本类型，它的getName方法返回的其实是他们的typename，而不是classname。
 * 平常使用的时候，经常需要对其进行复杂的判断，转换。
 * tn：原始类型类名，比如int、long，int[]，它们不能通过Class#forName得到对应的class，会报异常。只有cn才能通过Class#forName得到class。
 * cn：类名，比如java.lang.String,I,B,C,J。
 * icn：内部类名。比如I为int类型的内部类名。java/lang/String为java.lang.String的内部类名。
 * pcn:原始类型的类名。
 * desc：类描述符。比如Ljava/lang/String;为java.lang.String的类描述符。注意后面的分号不能省略。
 * @author wt
 */
public abstract class ClassUtil {
    private static Map<String,String> ptn2cn = new HashMap<>();
    private static Map<Type,Integer> pt2retOpCode = new HashMap<>();

    static{
        initPt2retOpCode();
        initPtn2cn();
    }
    private static void initPt2retOpCode(){
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
    private static void initPtn2cn(){
        ptn2cn.put("boolean","Z");
        ptn2cn.put("byte","B");
        ptn2cn.put("char","C");
        ptn2cn.put("short","S");
        ptn2cn.put("int","I");
        ptn2cn.put("long","J");
        ptn2cn.put("float","F");
        ptn2cn.put("double","D");
    }
    public static int typeToRetOpcode(Type type){
        Integer opcode = pt2retOpCode.get(type);
        if(opcode==null){
            return Opcodes.ARETURN;
        }
        return opcode;
    }
    private static String ptnToCn(String ptnOrCn){
        String cn = ptn2cn.get(ptnOrCn);
        if(cn != null){
            return cn;
        }
        return ptnOrCn;
    }
    public static String tnOrCnToCn(String tnOrCn){
        //eg:long[][] => [[J
        int i = tnOrCn.indexOf('[');
        if(i>0){
            String part1 = tnOrCn.substring(0,i);
            String part2 = tnOrCn.substring(i);
            String cn = part2.replaceAll("]","");
            cn = cn+ptnToCn(part1);
            return cn.replaceAll("/",".");
        }
        return tnOrCn.replaceAll("/",".");
    }
    public static Class forName(String tnOrCn) throws ClassNotFoundException {
        return Class.forName(tnOrCnToCn(tnOrCn));
    }

    public static String cnToDesc(String cn) {
        return cn+";";
    }
    public static String tnOrCnToDesc(String tnOrCn) {
        String cn = tnOrCnToCn(tnOrCn);
        return cnToDesc(cn);
    }
    public static String descToCn(String desc){
        return desc.replace(";","");
    }

    public static void main(String[] args) throws Exception {
        Class clazz = String[][].class;
        String s = clazz.getName();
        System.out.println(s);

        s = clazz.getTypeName();//String[][]=>[[Ljava/lang/String;
        System.out.println(s);
        s = Type.getDescriptor(String[][].class);
        System.out.println(s);
        //String=>Ljava/lang/String;
        //String[]=>[Ljava/lang/String;
        //String[][]=>[[Ljava/lang/String;
    }

}
