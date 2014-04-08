package org.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfiguration {

    @Value("${redis.hostname}") String hostname;
    @Value("${redis.password}") String password;
    @Value("${redis.port}") int port;    
    
    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        
    	JedisPoolConfig poolConfig = new JedisPoolConfig();
    	
    	JedisConnectionFactory jcf = new JedisConnectionFactory(poolConfig);
        jcf.setHostName(hostname);
        jcf.setPort(port);
        jcf.setPassword(password);
		jcf.setUsePool(true);
		
        return jcf;
    }

    @Bean
    public RedisTemplate<Object, Object> genericRedisTemplate() {
        
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        
        RedisSerializer<Object> convertor = new JdkSerializationRedisSerializer();
        
        redisTemplate.setKeySerializer(convertor);
        redisTemplate.setValueSerializer(convertor);
        
        return redisTemplate;
    }
   
}
