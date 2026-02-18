package oj.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 * 用于配置 Redis 连接工厂和 RedisTemplate
 */
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.database}")
    private int database;

    /**
     * 创建 Redis 连接工厂
     * @return LettuceConnectionFactory
     */
    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        org.springframework.data.redis.connection.RedisStandaloneConfiguration config = new org.springframework.data.redis.connection.RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setPassword(RedisPassword.of(password));
        config.setDatabase(database);
        return new LettuceConnectionFactory(config);
    }

    /**
     * 创建 RedisTemplate Bean，并设置自定义的序列化器
     * @param lettuceConnectionFactory 通过 @Bean 注解注入的连接工厂
     * @return RedisTemplate<String, Object>
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        // 1. 创建 RedisTemplate 对象
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // 2. 设置连接工厂
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);

        // 3. 配置 Jackson2JsonRedisSerializer 序列化器
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        // 配置 ObjectMapper 以支持更广泛的类型识别
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // 4. 配置 StringRedisSerializer 序列化器（用于 key）
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // 5. 设置 key 和 value 的序列化方式
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        // 6. 初始化 RedisTemplate
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }
}