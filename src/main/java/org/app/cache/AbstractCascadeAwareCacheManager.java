package org.app.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;

// volodymyr_krasnikov1 <vkrasnikov@gmail.com> 2:34:15 PM 

public abstract class AbstractCascadeAwareCacheManager extends AbstractCacheManager {
    
    private boolean cascadingEnabled = false; 

    public void setCascadingEnabled(boolean cascadingEnabled) {
        this.cascadingEnabled = cascadingEnabled;
    }
    
    public boolean isCascadingEnabled() {
        return cascadingEnabled;
    }
    
    @Override
    protected Cache decorateCache(Cache cache) {
        return isCascadingEnabled() ? new CascadeAwareCache(cache, cascadeCache( cache.getName() ) ) : cache;
    }
    
    protected abstract Cache cascadeCache(String name);

}
