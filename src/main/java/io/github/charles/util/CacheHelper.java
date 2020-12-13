package io.github.charles.util;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CacheHelper {

    private static final long THREE_MIN_IN_SECOND = 3 * 60;
    private static Logger logger = LoggerFactory.getLogger(CacheHelper.class);

    private static CacheManager  cacheManager;
    private static final String MESSAGE_BASE64_CACHE_NAME = "messageBash64";

    public CacheHelper() {
        cacheManager = CacheManagerBuilder
                .newCacheManagerBuilder()
                .build();
        cacheManager.init();

        cacheManager
                .createCache(MESSAGE_BASE64_CACHE_NAME, CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(
                                String.class, Integer.class,
                                ResourcePoolsBuilder.heap(10))
                        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(THREE_MIN_IN_SECOND))));
    }

    private Cache<String, Integer> geMmessageBash64CacheFromCacheManager() {

        return cacheManager.getCache(MESSAGE_BASE64_CACHE_NAME, String.class, Integer.class);
    }


    public boolean isDuplicateMessage(String base64Value) {
        return geMmessageBash64CacheFromCacheManager().containsKey(base64Value);
    }

    public void addMessageToCache(String base64Value) {
        geMmessageBash64CacheFromCacheManager().put(base64Value, 0);
    }

    private void printAll() {
        StreamSupport.stream(geMmessageBash64CacheFromCacheManager().spliterator(), false)
                .collect(Collectors.toMap(Cache.Entry::getKey, Cache.Entry::getValue)).forEach((k, e) -> logger.info(k));
    }
}
