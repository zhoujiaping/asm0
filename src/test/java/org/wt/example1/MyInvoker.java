package org.wt.example1;

public class MyInvoker {
    public static String example(){
        String cn = MyInvoker.class.getName();
        String[] parts = cn.split("\\.");
        return parts[parts.length-2];
    }
    public static void ivk(){
        System.out.println(example());
        //return new Date();
    }
}
