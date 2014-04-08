package org.app.cache;

import java.util.Collection;
import java.util.LinkedHashSet;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;

import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.util.Assert;


// volodymyr_krasnikov1 <vkrasnikov@gmail.com> 2:25:18 PM 

public class CascadeAwareCacheManager extends AbstractCascadeAwareCacheManager {
    
    private EhCacheCacheManager ehCacheCacheManager;
    private RedisCacheManager redisCacheManager;

    public CascadeAwareCacheManager(EhCacheCacheManager ehCacheCacheManager, RedisCacheManager redisCacheManager) {
        this.ehCacheCacheManager = ehCacheCacheManager;
        this.redisCacheManager = redisCacheManager;
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        
        CacheManager cacheManager = ehCacheCacheManager.getCacheManager();
        
        Assert.notNull(cacheManager, "A backing EhCache CacheManager is required");
        Status status = cacheManager.getStatus();
        Assert.isTrue(Status.STATUS_ALIVE.equals(status),
                "An 'alive' EhCache CacheManager is required - current cache is " + status.toString());

        String[] names = cacheManager.getCacheNames();
        Collection<Cache> caches = new LinkedHashSet<Cache>(names.length);
        for (String name : names) {
            caches.add(new EhCacheCache(cacheManager.getEhcache(name)));
        }
        return caches;
    }
    
    @Override
    public Cache getCache(String name) {
        
        CacheManager cacheManager = ehCacheCacheManager.getCacheManager();
        
        Cache cache = super.getCache(name);
        if (cache == null) {
            // check the EhCache cache again
            // (in case the cache was added at runtime)
            Ehcache ehcache = cacheManager.getEhcache(name);
            if (ehcache != null) {
                cache = new EhCacheCache(ehcache);
                addCache(cache);
            }
        }
        return cache;
    }

    @Override
    protected Cache cascadeCache(String name) {
        return redisCacheManager.getCache(name);
    }

}
