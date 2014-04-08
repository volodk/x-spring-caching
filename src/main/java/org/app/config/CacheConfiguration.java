package org.app.config;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;

import net.sf.ehcache.management.ManagementService;

import org.app.cache.CascadeAwareCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cache.interceptor.DefaultKeyGenerator;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@EnableCaching
public class CacheConfiguration implements CachingConfigurer {
	
	private static final Logger logger = LoggerFactory.getLogger(CacheConfiguration.class);
	
    @Value("${cache.useRedis}") boolean useRedis;
    @Value("${cache.enable}") boolean enableCaching;
    @Value("${cache.countStatistics}") boolean enableCachingStatistics;
    
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;
	
	@Bean
    public FactoryBean<net.sf.ehcache.CacheManager> ehCacheManagerFactoryBean() {
        EhCacheManagerFactoryBean factory = new EhCacheManagerFactoryBean();
        factory.setConfigLocation(new ClassPathResource("/ehcache.xml"));
        factory.setShared(true); 
        return factory;
    }
	
	@Bean
    public net.sf.ehcache.CacheManager nativeEhCacheManager() {
        try {
            return ehCacheManagerFactoryBean().getObject();
        } catch (Exception e) {
            logger.error("nativeEhCacheManager bean creation error", e);
            throw new IllegalStateException("Failed to create an native ehCache cache manager", e);
        }
    }
    
    @Bean
    @Override
    public org.springframework.cache.CacheManager cacheManager() {  // entry point to spring caching 
        
        if ( enableCaching ) {

            try {

                net.sf.ehcache.CacheManager nativeCacheManager = nativeEhCacheManager();
                if( enableCachingStatistics ){
                    registerAsMBean(nativeCacheManager);
                }
                EhCacheCacheManager ehcache = new EhCacheCacheManager( nativeCacheManager );
                RedisCacheManager redis = new RedisCacheManager( redisTemplate );

                CascadeAwareCacheManager cascadeCacheManager = new CascadeAwareCacheManager(ehcache, redis);
                cascadeCacheManager.setCascadingEnabled(useRedis);

                return cascadeCacheManager;

            } catch (Exception e) {
                throw new RuntimeException("Cannot init caching layer", e);
            }
            
        } else {
            return new NoOpCacheManager();
        }
    }

    @Override
    public KeyGenerator keyGenerator() {
        return new DefaultKeyGenerator();   // very important part !!! use proper key generator to avoid key clashes
    }
    
    private void registerAsMBean(net.sf.ehcache.CacheManager manager) {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        boolean registerCacheManager = false;
        boolean registerCaches = false;
        boolean registerCacheConfigurations = false;
        boolean registerCacheStatistics = true;
        ManagementService.registerMBeans(manager, mBeanServer, registerCacheManager, registerCaches,
                registerCacheConfigurations, registerCacheStatistics);
    }
}
