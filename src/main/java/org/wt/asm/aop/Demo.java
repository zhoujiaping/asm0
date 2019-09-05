package org.wt.asm.aop;

public class Demo {

   /* public void hello2() throws Throwable {
        //MyInvoker.map.get(Demo.class.getName()).invoke(this, "hello2");
        System.out.println(System.currentTimeMillis());

    }

   public void hello2_wt_pxy() throws Throwable {
       MyInvoker.map.get("clazzname").invoke(this,"hello2_wt_pxy",new String[]{},new Object[]{});

//       System.out.println(System.currentTimeMillis());
    }*/
 /*

    public void hello3() throws Throwable {
        MyInvoker.map.get("clazzname").invoke(this, "hello3");

    }

    public void hello3_wt_pxy() {
        System.out.println(System.currentTimeMillis());
    }

    public void hello4_wt_pxy(String name, int age, Demo another) {
        System.out.println(System.currentTimeMillis());
    }

    public void hello4(String name, int age, Demo another) throws Throwable {
        MyInvoker.map.get(Demo.class.getName()).invoke(this, "hello4", name, age, another);
    }

    public Map hello5_wt_pxy() {
        System.out.println(System.currentTimeMillis());
        return new HashMap<>();
    }

    public Map hello5() throws Throwable {
        return (Map) MyInvoker.map.get(Demo.class.getName()).invoke(this, "hello5");
    }

    public static Map hello6() throws Throwable {
        return (Map) MyInvoker.map.get(Demo.class.getName()).invoke(Demo.class, "hello6");
    }

    public List<Demo> hello1_wt_pxy(String name, int age, Demo another) throws Throwable {
        Thread.sleep(1000);
        List<Demo> list = new ArrayList<>();
        list.add(new Demo());
        return list;
    }
    private static final List<Demo> hello6(String name, int age, Demo another){
        return
    }
*/
    public void hello1(String name, int i,Demo another){
    }
    public static void test(String[] args){
        System.out.println("test static method");
    }
}
