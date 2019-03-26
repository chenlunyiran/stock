package com.twotiger.stock.cache.spring.redis;

import org.springframework.data.redis.connection.DefaultTuple;
import org.springframework.data.redis.connection.RedisZSetCommands;


public class DefaultObjectTuple extends DefaultTuple implements ObjectRedisConnection.ObjectTuple {

    private final Object valueAsObject;

    /**
     * Constructs a new <code>DefaultStringTuple</code> instance.
     *
     * @param value
     * @param score
     */
    public DefaultObjectTuple(byte[] value, Object valueAsObject, Double score) {
        super(value, score);
        this.valueAsObject = valueAsObject;

    }

    /**
     * Constructs a new <code>DefaultStringTuple</code> instance.
     *
     * @param tuple
     * @param valueAsObject
     */
    public DefaultObjectTuple(RedisZSetCommands.Tuple tuple, Object valueAsObject) {
        super(tuple.getValue(), tuple.getScore());
        this.valueAsObject = valueAsObject;
    }

    public Object getValueAsObject() {
        return valueAsObject;
    }

    public String toString() {
        return "DefaultStringTuple[value=" + getValueAsObject() + ", score=" + getScore() + "]";
    }
}
