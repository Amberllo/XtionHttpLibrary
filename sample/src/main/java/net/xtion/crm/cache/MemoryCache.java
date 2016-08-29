package net.xtion.crm.cache;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Amberllo on 2016/8/29.
 */
public class MemoryCache {

    static MemoryCache instance;

    public static  MemoryCache getInstance(){
        if(instance == null){
            instance = new MemoryCache();
        }
        return instance;
    }

    Map<String,String> cache = new HashMap<String,String>();

    public String get(String key){
        String str = cache.get(key);
        return TextUtils.isEmpty(str)?"":str;
    }

    public void put(String key,String value){
        cache.put(key,value);
    }

}
