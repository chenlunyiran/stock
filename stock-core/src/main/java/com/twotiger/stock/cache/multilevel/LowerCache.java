package com.twotiger.stock.cache.multilevel;

import com.twotiger.stock.cache.api.CacheApi;
import org.springframework.util.StringUtils;


/**
 * 底层的缓存
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2015/11/11
 * Time: 21:59
 * To change this template use File | Settings | File Templates.
 */
public interface LowerCache extends CacheApi{
    String ALL_REMOVE_PREFIX = "_ALL_REMOVE_";
    String CACHE_CHANNEL_PREFIX = "_CACHE_CHANNEL_";

    /**
     * 删除平级的缓存(包含自己,优先移除自己)，可以是异步的，需要保证最终删除
     */
    void removeLateral(String key);

    /**
     * 接收删除key通知
     *
     * @param key
     * @return
     */
    default boolean onRemove(String key) {
        if (!StringUtils.isEmpty(key)) {
            if (key.equals(ALL_REMOVE_PREFIX + name())) {
                removeAll();
                return true;
            } else {
                return remove(key);
            }
        }
        return false;
    }

    String name();
}
