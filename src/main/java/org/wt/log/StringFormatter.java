package org.wt.log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringFormatter {
    /**
     * 实现类似slf4j的参数化效果。
     * 实际用途，项目中使用slf4j，打印日志的时候采用"{}"占位符。但是java的String.format并不支持这种方式，使得代码中出现两种风格的占位符方式（String.format一般用"%s"方式）。特别是需要将String.format的内容作为日志打印的时候。如果能够统一风格，那么代码中将String.format和logger.info的模板字符串进行迁移就非常方便了。
     * slf4j的logger.error();方法，没有error(Throwable e,String msg,Object... args);版本
     * */
    public static String format(String format,Object... args){
        Pattern RENDER_PATTERN = Pattern.compile("\\{\\}",Pattern.MULTILINE);
        if(format==null){
            return null;
        }
        Matcher matcher = RENDER_PATTERN.matcher(format);
        int index = 0;
        int argsIndex = 0;
        int argsLen = args.length;
        StringBuilder sb = new StringBuilder();
        int start = -1;
        Object value = null;
        while(matcher.find(index)){
            start = matcher.start();
            if(argsIndex>=argsLen){
                break;
            }
            value = args[argsIndex];
            sb.append(format.substring(index, start)).append(value);
            index = matcher.end();
            argsIndex++;
        }
        sb.append(format.substring(index));
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(format("hello{}",1,2,3));
        System.out.println(format("hello{}{}{}{}{}",1,2,3));
        System.out.println(format("hello{a}{}{}",1,2,3));
    }
}
