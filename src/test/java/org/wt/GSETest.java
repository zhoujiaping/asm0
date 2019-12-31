package org.wt;

import groovy.lang.Binding;
import groovy.lang.GroovyObject;
import groovy.util.GroovyScriptEngine;

import java.io.IOException;
import java.util.Date;

public class GSETest {
    public static void main(String[] args) throws Exception {
        String root = GSETest.class.getResource("/").getFile();
        String[] roots  =   new  String[] {  root  };
        GroovyScriptEngine gse  =  new  GroovyScriptEngine(roots);
        Binding binding  =  new  Binding();
        binding.setVariable( " input " ,  " world " );
        while(true){
            System.out.println(new Date());
            gse.run( " mock/main.groovy " , binding);
            //System.out.println(binding.getVariable( " output " ));
            Thread.sleep(2000);
        }
    }
}
