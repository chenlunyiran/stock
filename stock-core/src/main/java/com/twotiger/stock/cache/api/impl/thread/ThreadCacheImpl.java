package com.twotiger.stock.cache.api.impl.thread;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 线程缓存
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2015/11/22
 * Time: 18:14
 * To change this template use File | Settings | File Templates.
 */
public class ThreadCacheImpl {
    Set<Set<String>> notifySets;

    class CacheEntity {
        Map<String, Object> cacche = new HashMap<>();
        Set<String> removeKeys = new HashSet<>();//in notifySets
    }
}
