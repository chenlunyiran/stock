package com.twotiger.stock.cache.jedis;

import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;
import redis.clients.jedis.Jedis;

@FunctionalInterface
public interface JedisCallback<T> {
    @Nullable
    T doInJedis(Jedis jedis) throws DataAccessException;
}
