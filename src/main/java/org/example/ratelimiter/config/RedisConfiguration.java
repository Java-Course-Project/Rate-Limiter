package org.example.ratelimiter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ratelimiter.model.TokenBucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {

	@Bean
	public RedisTemplate<String, TokenBucket> redisTemplate(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {
		Jackson2JsonRedisSerializer<TokenBucket> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, TokenBucket.class);

		RedisTemplate<String, TokenBucket> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(serializer);
		template.setHashValueSerializer(serializer);
		template.setEnableTransactionSupport(true);
		return template;
	}
}
