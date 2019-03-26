package com.twotiger.stock.cache.multilevel.lowerjcache;


import com.twotiger.stock.cache.api.impl.jcache.JCacheImpl;
import com.twotiger.stock.cache.multilevel.LowerCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import javax.cache.Cache;
import java.nio.charset.Charset;

/**
 * redis sub/pub jcache
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2015/11/18
 * Time: 0:06
 * To change this template use File | Settings | File Templates.
 */
public class LowerRedisCacheImpl extends JCacheImpl implements LowerCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(LowerRedisCacheImpl.class);
    private static final Charset CHARSET = Charset.forName("utf-8");

    private final RedisTemplate redisTemplate;

    public LowerRedisCacheImpl(Cache cache, RedisTemplate redisTemplate) {
        super(cache);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void removeLateral(String key) {
        remove(key);
        try {
            redisTemplate.convertAndSend(CACHE_CHANNEL_PREFIX + name(), key);
        } catch (Exception e) {
            LOGGER.error("send syn message error!key=" + key, e);
        }
    }

    @Override
    public String name() {
        return cacheName;
    }

    @Override
    public void destroy() throws Exception {
        try {
            super.destroy();
        } finally {

        }
    }
}
