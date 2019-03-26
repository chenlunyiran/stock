package com.twotiger.stock.cache.spring.cache;

import com.twotiger.stock.cache.api.CacheApi;
import com.twotiger.stock.cache.api.impl.redis.RedisImpl;
import com.twotiger.stock.cache.multilevel.LevelCache;
import com.twotiger.stock.cache.multilevel.LevelCacheImpl;
import com.twotiger.stock.cache.multilevel.LowerCache;
import com.twotiger.stock.cache.multilevel.lowerjcache.LowerRedisCacheImpl;
import com.twotiger.stock.cache.spring.redis.ObjectRedisTemplate;
import org.ehcache.config.CacheRuntimeConfiguration;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.ResourceType;
import org.ehcache.config.ResourceUnit;
import org.ehcache.config.SizedResourcePool;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import javax.cache.configuration.MutableConfiguration;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class SpringLevelCacheManager implements CacheManager,MessageListener {

    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>();

    private final ObjectRedisTemplate objectRedisTemplate;

    //缓存已经申请的总内存
    private static final AtomicLong MEMORY= new AtomicLong(0);
    //支持最大内存
    private long maxMemory = 100;

    @Autowired
    private javax.cache.CacheManager jCacheManager;

    public SpringLevelCacheManager(ObjectRedisTemplate objectRedisTemplate) {
        this.objectRedisTemplate = objectRedisTemplate;
    }

    @Override
    public Cache getCache(String name) {
        return cacheMap.computeIfAbsent(name, (key -> {
            //1
//<bean id="jCacheManager"
//            class="org.springframework.cache.jcache.JCacheManagerFactoryBean">
//    <property name="cacheManagerUri" value="classpath:ehcache.xml" />
//</bean>
//<bean id="cacheManager" class="org.springframework.cache.jcache.JCacheCacheManager">
//    <property name="cacheManager" ref="jCacheManager" />
//</bean>
            //2
//            CachingProvider provider = Caching.getCachingProvider();
//            CacheManager cacheManager = provider.getCacheManager();
//            MutableConfiguration<Long, String> configuration =
//                    new MutableConfiguration<Long, String>()
//                            .setTypes(Long.class, String.class)
//                            .setStoreByValue(false)
//                            .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_MINUTE));
//            Cache<Long, String> cache = cacheManager.createCache("jCache", configuration);
//            cache.put(1L, "one");
//            String value = cache.get(1L);

            //3
//            MutableConfiguration<Long, String> configuration = new MutableConfiguration<Long, String>();
//            configuration.setTypes(Long.class, String.class);
//            Cache<Long, String> cache = cacheManager.createCache("someCache", configuration);
//
//            CompleteConfiguration<Long, String> completeConfiguration = cache.getConfiguration(CompleteConfiguration.class);
//
//            Eh107Configuration<Long, String> eh107Configuration = cache.getConfiguration(Eh107Configuration.class);
//
//            CacheRuntimeConfiguration<Long, String> runtimeConfiguration = eh107Configuration.unwrap(CacheRuntimeConfiguration.class);

            //4
//            CacheConfiguration<Long, String> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class,
//                    ResourcePoolsBuilder.heap(10)).build();
//
//            Cache<Long, String> cache = cacheManager.createCache("myCache",
//                    Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfiguration));
//
//            Eh107Configuration<Long, String> configuration = cache.getConfiguration(Eh107Configuration.class);
//            configuration.unwrap(CacheConfiguration.class);
//
//            configuration.unwrap(CacheRuntimeConfiguration.class);
//
//            try {
//                cache.getConfiguration(CompleteConfiguration.class);
//                throw new AssertionError("IllegalArgumentException expected");
//            } catch (IllegalArgumentException iaex) {
//                // Expected
//            }



            //5
//            <config
//            xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
//            xmlns='http://www.ehcache.org/v3'
//            xsi:schemaLocation="
//            http://www.ehcache.org/v3 http://www.ehcache.org/schema/ehcache-core-3.0.xsd">
//
//  <cache alias="ready-cache">
//    <key-type>java.lang.Long</key-type>
//    <value-type>com.pany.domain.Product</value-type>
//    <loader-writer>
//      <class>com.pany.ehcache.integration.ProductCacheLoaderWriter</class>
//    </loader-writer>
//    <heap unit="entries">100</heap>
//  </cache>
//
//</config>
//                    CachingProvider cachingProvider = Caching.getCachingProvider();
//            CacheManager manager = cachingProvider.getCacheManager(
//                    getClass().getResource("/org/ehcache/docs/ehcache-jsr107-config.xml").toURI(),
//                    getClass().getClassLoader());
//            Cache<Long, Product> readyCache = manager.getCache("ready-cache", Long.class, Product.class);

            //todo build and config and validate maxMemory  http://www.ehcache.org/documentation/3.0/107.html
            javax.cache.Cache<String,Object>  jCache  = jCacheManager.getCache(key);//read from ehcache.xml
            if(jCache!=null){
                //Get to the JSR-107 CompleteConfiguration
                //CompleteConfiguration<String, Object> completeConfiguration = jCache.getConfiguration(CompleteConfiguration.class);
                //Get to the Ehcache JSR-107 configuration bridge
                Eh107Configuration<String, Object> eh107Configuration = jCache.getConfiguration(Eh107Configuration.class);
                CacheRuntimeConfiguration<String, Object> runtimeConfiguration = eh107Configuration.unwrap(CacheRuntimeConfiguration.class);
                ResourcePools resourcePools = runtimeConfiguration.getResourcePools();
                SizedResourcePool sizedResourcePool = resourcePools.getPoolForResource(ResourceType.Core.HEAP);
                long size = sizedResourcePool.getSize();
                ResourceUnit unit = sizedResourcePool.getUnit();
                if(unit instanceof MemoryUnit){
                    MemoryUnit memoryUnit = (MemoryUnit)unit;
                    long memory = memoryUnit.convert(size,MemoryUnit.MB);
                    MEMORY.addAndGet(memory);
                }
            }else{
                MutableConfiguration<String, Object> configuration = new MutableConfiguration<String, Object>();
                //set maxMemory
                //++maxMemory
                jCache = jCacheManager.createCache(key,configuration);
            }
            validateMaxMemory();
            LowerCache lowerCache = new LowerRedisCacheImpl(jCache, objectRedisTemplate);
            CacheApi upperCache = new RedisImpl(key,objectRedisTemplate);
            LevelCache levelCache = new LevelCacheImpl(key, upperCache, lowerCache);
            return new StringLevelCache(levelCache);
        }));
    }

    private void validateMaxMemory() {
        if(MEMORY.longValue()>maxMemory){

        }
    }

    @Override
    public Collection<String> getCacheNames() {
        return cacheMap.keySet();
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        String channel = objectRedisTemplate.getStringSerializer().deserialize(message.getChannel());
        String cacheName = channel.replaceFirst(LowerCache.CACHE_CHANNEL_PREFIX,"");
        String key = (String)objectRedisTemplate.getValueSerializer().deserialize(message.getBody());
        Cache cache = cacheMap.get(key);

        LevelCache levelCache = (LevelCache)cache.getNativeCache();
        //levelCache.getUpper().o
    }
}
