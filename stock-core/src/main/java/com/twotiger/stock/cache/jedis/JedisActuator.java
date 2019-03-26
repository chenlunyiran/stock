package com.twotiger.stock.cache.jedis;

import org.springframework.lang.Nullable;

public interface JedisActuator {
    @Nullable
    <T> T execute(JedisCallback<T> jedisCallback);
}
