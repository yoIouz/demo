package by.test.sample.configuration;

import by.test.sample.dto.UserFilter;
import by.test.sample.enums.SearchEngineType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

import static by.test.sample.utils.ApplicationConstants.REDIS_TTL_MINUTES;

@Configuration
public class RedisConfiguration {

    @Value("${search.use-elastic:false}")
    private boolean useElastic;

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

//    @Bean
//    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, String> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(new StringRedisSerializer());
//        return template;
//    }

    @Bean("userKeyGenerator")
    public KeyGenerator userKeyGenerator() {
        return (target, method, params) -> {
            UserFilter filter = (UserFilter) params[0];
            Pageable pageable = (Pageable) params[1];
            String engine = SearchEngineType.getEngine(useElastic).toString();
            return filter.toString() + ":" + pageable.getPageNumber() + ":" +
                    pageable.getPageSize() + ":" + engine;
        };
    }
}
