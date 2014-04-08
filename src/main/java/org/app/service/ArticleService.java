package org.app.service;

import org.app.domain.Article;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

// volodymyr_krasnikov1 <vkrasnikov@gmail.com> 1:15:56 PM 

@Service
public class ArticleService {

    @Cacheable("articleCache")
    public Article get(long id){
        
        return heavilyLoadedObject();
    }

    @SuppressWarnings("serial")
    private Article heavilyLoadedObject() {
        
        return new Article(){
                
            @Override public long getId() {
                return 101L;
            }
            
            @Override public String getTitle() {
                return "object from relatively slow datasource";
            }
        };
    }
}
