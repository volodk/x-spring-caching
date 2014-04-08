package org.app.domain;

import java.io.Serializable;

// volodymyr_krasnikov1 <vkrasnikov@gmail.com> 1:14:08 PM 

public class Article implements Serializable{

    private static final long serialVersionUID = -7890719951437678872L;
    
    long id;
    
    String title;
    
    // etc
    
    public long getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }

}
