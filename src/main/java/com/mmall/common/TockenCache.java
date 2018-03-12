package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhaoxin on 2018/3/8.
 */
public class TockenCache {
    private static Logger logeger=LoggerFactory.getLogger(TockenCache.class);

    public static final String TOCKEN_PREFIX="token_";
    //LRU算法
    private static LoadingCache<String,String> localCache= CacheBuilder.newBuilder()
            .initialCapacity(1000)
            .maximumSize(10000)
            .expireAfterAccess(12, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                //默认数据加载实现,当调用get取值的时候 如果没有对应的值就调用
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });
    public static void setKey(String key,String value){
        localCache.put(key,value);
    }

    public static String getByKey(String key){
        String value=null;
        try {
            value=localCache.get(key);
            if ("null".equals(value)){
                return null;
            }
            return value;
        } catch (Exception e) {
           logeger.error("localCache get error",e);
        }
        return null;
    }
}
