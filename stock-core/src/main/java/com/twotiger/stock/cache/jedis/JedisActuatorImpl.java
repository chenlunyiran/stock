package com.twotiger.stock.cache.jedis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;

public class JedisActuatorImpl implements JedisActuator {
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public <T> T execute(JedisCallback<T> jedisCallback) {
        return (T) redisTemplate.execute((RedisConnection redisConnection) -> {
            return jedisCallback.doInJedis((Jedis)redisConnection.getNativeConnection());
        });
    }

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
