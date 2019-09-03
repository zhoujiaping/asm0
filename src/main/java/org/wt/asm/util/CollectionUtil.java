package org.wt.asm.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class CollectionUtil {
    public static <K,V> Map<V,K> reverseKeyValue(Map<K,V> map){
        if(map==null){
            return null;
        }
        Set<Map.Entry<K,V>> es = map.entrySet();
        Map<V,K> reversedMap = new HashMap<>();
        for(Map.Entry<K,V> e : es){
            reversedMap.put(e.getValue(),e.getKey());
        }
        return reversedMap;
    }
}
