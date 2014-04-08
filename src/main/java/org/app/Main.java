package org.app;

import org.app.domain.Article;
import org.app.service.ArticleService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

// volodymyr_krasnikov1 <vkrasnikov@gmail.com> 12:17:49 PM 

public class Main {

    public static void main(String[] args) {
        
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext("org.app.config");
        ctx.registerShutdownHook();
        try{
            ctx.start();
            
            runMe(ctx);
            
        } finally{
            ctx.stop();
        }
    }

    private static void runMe(ApplicationContext ctx) {
        
        ArticleService service = ctx.getBean(ArticleService.class);
        
        Article a = service.get( 101L );    // cache miss
        
        Article b = service.get( 101L );    // cache hit
        
    }
}
