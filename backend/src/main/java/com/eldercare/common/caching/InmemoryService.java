package com.eldercare.common.caching;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InmemoryService implements CachingService{
    private ConcurrentHashMap<String, Object> template;

    private InmemoryService(ConcurrentHashMap<String, Object> template){
        this.template = template;
    }

    @Bean
    public InmemoryService inmemoryService(){
            return new InmemoryService(new ConcurrentHashMap<String,Object>());
    }

    @Override
    public void set(String key, Object value) {
        this.template.put(key,value);
    }

    @Override
    public void set(String key, Object value, int seconds) {
        this.template.put(key,value);
    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public void delete(String key) {

    }
}
