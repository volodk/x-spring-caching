package org.app.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.util.Assert;

// volodymyr_krasnikov1 <vkrasnikov@gmail.com> 2:29:15 PM 

public class CascadeAwareCache implements Cache {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CascadeAwareCache.class);
    
    private final Cache ehCache, redisCache;

    public CascadeAwareCache(Cache ehCache, Cache redisCache) {
        Assert.notNull(ehCache, "ehCache must not be null");
        Assert.notNull(redisCache, "redis Cache must not be null");
        this.ehCache = ehCache;
        this.redisCache = redisCache;
    }

    @Override
    public String getName() {
        return ehCache.getName();
    }

    @Override
    public Object getNativeCache() {
        return ehCache.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper vw = ehCache.get(key);
        if( vw == null ){
            LOGGER.info("[EhCache] cache miss for a key : " + key + ", commencing with redis ...");
            
            try {
                vw = redisCache.get(key);
            } catch( RuntimeException e){
                LOGGER.warn("Exception on redis GET", e);
            }
            
            if( vw == null ) LOGGER.info("[Redis] cache miss for a key : " + key);
            else {
                LOGGER.debug("Entry key: " + key + " was found in redis, coping to ehCahe ...");
                ehCache.put( key, vw.get() );
            }
        }
        return vw;
    }

    @Override
    public void put(final Object key, final Object value) {
        if(LOGGER.isDebugEnabled())
            LOGGER.debug("[EhCache] PUT " + String.format("(key: %s, value: %s)", key, value));
        ehCache.put(key, value);
        
        if(LOGGER.isDebugEnabled())
            LOGGER.debug("[Redis] PUT " + String.format("(key: %s, value: %s)", key, value));
        try{
            redisCache.put(key, value);
        } catch( RuntimeException e ){
            LOGGER.warn("Exception on redis PUT", e);
        }
    }

    @Override
    public void evict(final Object key) {
        if(LOGGER.isDebugEnabled())
            LOGGER.debug("[EhCache] Evicting key: " + key);
        ehCache.evict(key);
        
        if(LOGGER.isDebugEnabled())
            LOGGER.debug("[Redis] Evicting key: " + key);
       
        try{
            redisCache.evict(key);
        } catch( RuntimeException e ){
            LOGGER.warn("Exception on redis EVICT", e);
        }
    }

    @Override
    public void clear() {
        if(LOGGER.isDebugEnabled())
            LOGGER.debug("[EhCache] Clear cache, name : " + getName() );
        ehCache.clear();
        
        if(LOGGER.isDebugEnabled())
            LOGGER.debug("[Redis] Clear cache, name : " + getName());
        
        try{
            redisCache.clear();
        } catch( RuntimeException e ){
            LOGGER.warn("Exception on redis CLEAR", e);
        }
        
    }

}
