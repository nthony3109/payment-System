package com.payement.wallet.Configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory, ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        GenericJacksonJsonRedisSerializer valueSerializer = new GenericJacksonJsonRedisSerializer(objectMapper);

        //to store key as string in Redis
        template.setKeySerializer(keySerializer);
        //to store obj in JSON format using the valueSerializer
        template.setValueSerializer(valueSerializer);
        //to tell spring inject this only after properties are set
        template.afterPropertiesSet();

        return template;
    }

    @Bean
    public RedisCacheManager cacheManager (RedisConnectionFactory factory) {
        RedisCacheConfiguration configuration =  RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // Set the cache expiration time
                .disableCachingNullValues(); // Disable caching of null values

        return RedisCacheManager.builder(factory)
                .cacheDefaults(configuration)
                .build();
    }
}
