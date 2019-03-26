package com.twotiger.stock.cache.multilevel;

import com.twotiger.stock.cache.api.CacheApi;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

import static com.twotiger.stock.cache.multilevel.LowerCache.ALL_REMOVE_PREFIX;

/**
 * 层级的缓存  上下层级 两级
 * 原则
 * 1 本级无值则以上级缓存值为准
 * 2 本级冲突以以前设置的值为准
 * 3 本级更新顺序上级更新成功后删除本级，以上级值为准
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2015/11/11
 * Time: 20:42
 * To change this template use File | Settings | File Templates.
 */
public interface LevelCache extends CacheApi{
    /**
     * 获取上一层级的缓存
     * @return
     */
    CacheApi getUpper();

    /**
     * 获取下层的缓存
     * @return
     */
    LowerCache getLower();

    @Override
    default boolean exists(String key){
        return get(key)!=null;
    }

    @Override
    default Object get(String key) {
        Object value;
        LowerCache lower = getLower();
        value = lower.get(key);
        if(value!=null){
            return value;
        }
        value = getUpper().get(key);
        if(value!=null){
            if(lower.putIfAbsent(key,value)){//TODO  UPPER get>remove  LOWER null>set  (last_version??lazyDelete)
                LoggerFactory.getLogger(getClass()).debug("{}load key={} from upper",lower.name(),key);
               return value;
            }else{//TODO  version object
                Object localValue = lower.get(key);
                LoggerFactory.getLogger(getClass()).info("{}load key={} from upper clash! loadValue={} localValue={}", lower.name(), key, value, localValue);
                return localValue;
            }
        }
        return value;
    }

    @Override
    default Object get(String key, Supplier init) {
        Object value = get(key);
        if(value==null){
            CacheApi upper = getUpper();
            value = upper.get(key,init);
        }
        return value;
    }

    @Override
    default Object getAndPut(String key, Object newValue) {
        try {
             return getUpper().getAndPut(key, newValue);
        }finally {
            getLower().removeLateral(key);
        }
    }

    @Override
    default boolean replace(String key, Object oldValue, Object newValue) {
        try{
            return getUpper().replace(key, oldValue, newValue);
        }finally {
            getLower().removeLateral(key);
        }
    }

    @Override
    default Object getAndRemove(String key) {
        try{
            return getUpper().getAndRemove(key);
        }finally {
            getLower().removeLateral(key);
        }
    }

    @Override
    default boolean remove(String key, Object oldValue) {
        try{
            return getUpper().remove(key,oldValue);
        }finally {
            getLower().removeLateral(key);
        }
    }

    @Override
    default boolean remove(String key){
        try {
            return getUpper().remove(key);
        }finally {
            getLower().removeLateral(key);
        }
    }

    @Override
    default Object getAndReplace(String key, Object newValue) {
        try {
            return getUpper().getAndReplace(key, newValue);
        }finally {
            getLower().removeLateral(key);
        }
    }

    @Override
    default void put(String key, Object value) {
        try {
            getUpper().put(key, value);
        }finally {
            getLower().removeLateral(key);
        }
    }

    @Override
    default boolean putIfAbsent(String key, Object value) {
        try {
            return getUpper().putIfAbsent(key, value);
        }finally {
            getLower().removeLateral(key);
        }
    }

    @Override
    default void removeAll() {
        try {
            getUpper().removeAll();
            getLower().removeAll();
        } finally {
            getLower().removeLateral(LowerCache.ALL_REMOVE_PREFIX + getLower().name());
        }
    }

}
