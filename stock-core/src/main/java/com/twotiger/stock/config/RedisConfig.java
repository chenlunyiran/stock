package com.twotiger.stock.config;

import com.twotiger.stock.cache.api.CacheApi;
import com.twotiger.stock.cache.api.LockApi;
import com.twotiger.stock.cache.api.impl.lock.LockRedissonImpl;
import com.twotiger.stock.cache.api.impl.redis.RedisImpl;
import com.twotiger.stock.cache.jedis.JedisActuator;
import com.twotiger.stock.cache.jedis.JedisActuatorImpl;
import com.twotiger.stock.cache.spring.redis.ObjectRedisTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.*;
import redis.clients.jedis.JedisPoolConfig;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.database}")
    private String database;



    @Bean
    @ConfigurationProperties(prefix = "spring.redis.pool")
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        return config;
    }

    @Bean(destroyMethod = "destroy")
    @Primary
    public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
        RedisStandaloneConfiguration redisStandaloneConfiguration  = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(Integer.valueOf(port));
        redisStandaloneConfiguration.setPassword(password);
        redisStandaloneConfiguration.setDatabase(Integer.valueOf(database));
        JedisConnectionFactory factory = new JedisConnectionFactory(redisStandaloneConfiguration);
        factory.setPoolConfig(jedisPoolConfig);
        return factory;
    }

    /**
     * 使用注意事项   不需要调用close方法
     * @return
     */
    @Bean
    public JedisActuator jedisActuator(){
        return new JedisActuatorImpl();
    }

    /**
     * <String String>
     * StringRedisTemplate and default RedisTemplate
     * @return
     */
    @Bean("redisTemplate")
    @Primary
    public StringRedisTemplate redisTemplate(JedisConnectionFactory jedisConnectionFactory ) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(jedisConnectionFactory);
        initRedisTemplate(stringRedisTemplate);
        return stringRedisTemplate;
    }

    /**
     * <String Object>
     *objectRedisTemplate
     * @return
     */
    @Bean("objectRedisTemplate")
    public ObjectRedisTemplate objectRedisTemplate(JedisConnectionFactory jedisConnectionFactory ) {
        ObjectRedisTemplate objectRedisTemplate = new ObjectRedisTemplate(jedisConnectionFactory);
        initRedisTemplate(objectRedisTemplate);
        return objectRedisTemplate;
    }

    private void initRedisTemplate(RedisTemplate redisTemplate) {
        redisTemplate.setKeySerializer(getRedisKeySerializer());
        redisTemplate.setValueSerializer(getRedisKeySerializer());
        redisTemplate.setHashKeySerializer(getRedisKeySerializer());
        redisTemplate.setHashValueSerializer(getRedisKeySerializer());
    }


    @Bean("redisKeySerializer")
    public RedisSerializer getRedisKeySerializer() {
        return stringRedisSerializer;
    }


    @Bean("redisValueSerializer")
    public RedisSerializer getRedisValueSerializer() {
        return genericJackson2JsonRedisSerializer;
    }
    private static final StringRedisSerializer stringRedisSerializer = new StringRedisSerializer(StandardCharsets.UTF_8);
    private static final JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
    private static final GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();

    @Bean
    @Primary
    public CacheManager cacheManager(JedisConnectionFactory jedisConnectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        redisCacheConfiguration = redisCacheConfiguration.prefixKeysWith("redis_cache");
        redisCacheConfiguration = redisCacheConfiguration.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(getRedisKeySerializer()));
        redisCacheConfiguration = redisCacheConfiguration.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(getRedisValueSerializer()));
        RedisCacheManager redisCacheManager =
            RedisCacheManager.builder(jedisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                //.initialCacheNames(null)
                //.transactionAware()
                //.withInitialCacheConfigurations(null)
                // .disableCreateOnMissingCache()
                .build();
        return redisCacheManager;
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }


    @Bean
    public CacheApi cacheApi(@Qualifier("objectRedisTemplate")  ObjectRedisTemplate objectRedisTemplate){
        CacheApi  cacheApi = new RedisImpl("CacheApiRedisImpl",objectRedisTemplate);
        return cacheApi;
    }

    @Bean
    public LockApi lockApi(){
        LockApi lockApi = new LockRedissonImpl(this.host,this.port,this.password);
        return lockApi;
    }

//    @Bean
//    @Override
//    public CacheResolver cacheResolver() {
//        return super.cacheResolver();
//    }
//    @Bean
//    @Override
//    public CacheErrorHandler errorHandler() {
//        return super.errorHandler();
//    }


//    二级缓存使用
//    @Bean
//    javax.cache.CacheManager jCacheManager(){
//        JCacheManagerFactoryBean jCacheManagerFactoryBean = new JCacheManagerFactoryBean();
//        //  read yml spring.cache.jcache.config  default classpath:ehcache.xml
//        jCacheManagerFactoryBean.setCacheManagerUri(URI.create("classpath:ehcache.xml"));
//    }
}
