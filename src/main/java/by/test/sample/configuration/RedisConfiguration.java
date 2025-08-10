package by.test.sample.configuration;

import by.test.sample.dto.UserFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

import static by.test.sample.utils.ApplicationConstants.REDIS_TTL_MINUTES;

@Configuration
public class RedisConfiguration {

    @Value("${search.engine}")
    private String engineType;

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        return RedisCacheManager.builder(factory)
                .cacheDefaults(this.redisCacheConfiguration())
                .build();
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(REDIS_TTL_MINUTES))
                .disableCachingNullValues();
    }

    @Bean("userKeyGenerator")
    public KeyGenerator userKeyGenerator() {
        return (target, method, params) -> {
            UserFilter filter = (UserFilter) params[0];
            Pageable pageable = (Pageable) params[1];
            return filter.toString() + ":" + pageable.getPageNumber() + ":" +
                    pageable.getPageSize() + ":" + engineType;
        };
    }
}
