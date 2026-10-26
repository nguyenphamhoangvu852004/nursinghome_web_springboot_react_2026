package com.eldercare.common.caching;

public interface CachingService {
    void set(String key, Object value);

    void set(String key, Object value, int seconds);

    Object get(String key);

    void delete(String key);
}
