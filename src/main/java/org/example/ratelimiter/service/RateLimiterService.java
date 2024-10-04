package org.example.ratelimiter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ratelimiter.config.RateLimiterProperties;
import org.example.ratelimiter.model.TokenBucket;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimiterService {
	private final RateLimiterProperties rateLimiterProperties;
	private final RedisTemplate<String, TokenBucket> redisTemplate;
	private static final String RATE_LIMITER_KEY = "rate-limiter";

	public boolean tryAcquire(int token) {
		List<String> keys = List.of(RATE_LIMITER_KEY);
		List<Object> args = List.of(System.currentTimeMillis(),
									rateLimiterProperties.getRefillRate().toMillis(), token,
									rateLimiterProperties.getBucketCapacity(), rateLimiterProperties.getBucketSize());


		return Boolean.TRUE.equals(
				redisTemplate.execute(RedisScript.of(new ClassPathResource("lua/check_and_set_rate_limiter.lua"), Boolean.class), keys,
									  args.toArray(new Object[0])));
	}
}
