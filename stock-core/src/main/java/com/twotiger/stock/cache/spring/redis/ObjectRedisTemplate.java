package com.twotiger.stock.cache.spring.redis;

import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.StandardCharsets;

public class ObjectRedisTemplate extends RedisTemplate<String, Object> {
    public ObjectRedisTemplate() {
        RedisSerializer<String> keySerializer = new StringRedisSerializer(StandardCharsets.UTF_8);
        RedisSerializer<?> valueSerializer = new JdkSerializationRedisSerializer();
        this.setKeySerializer(keySerializer);
        this.setValueSerializer(valueSerializer);
        this.setHashKeySerializer(keySerializer);
        this.setHashValueSerializer(valueSerializer);
    }

    public ObjectRedisTemplate(RedisConnectionFactory connectionFactory) {
        this();
        this.setConnectionFactory(connectionFactory);
        this.afterPropertiesSet();
    }

    protected RedisConnection preProcessConnection(RedisConnection connection, boolean existingConnection) {
        return new DefaultStringRedisConnection(connection);
    }
}
