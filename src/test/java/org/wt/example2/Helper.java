package org.wt.example2;

public class Helper {
    public static String example(){
        String cn = MyInvoker.class.getName();
        String[] parts = cn.split("\\.");
        return parts[parts.length-2];
    }
}
