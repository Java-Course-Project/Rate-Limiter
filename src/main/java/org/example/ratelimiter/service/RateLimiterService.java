package org.example.ratelimiter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ratelimiter.config.RateLimiterProperties;
import org.example.ratelimiter.model.TokenBucket;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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
	private final Resource luaScriptResource = new ClassPathResource("lua/check_and_set_rate_limiter.lua");

	public boolean tryAcquire(int token) {
		List<String> keys = List.of(RATE_LIMITER_KEY);
		List<Object> args = List.of(System.currentTimeMillis(),
									rateLimiterProperties.getRefillRate().toMillis(), token,
									rateLimiterProperties.getBucketCapacity(), rateLimiterProperties.getBucketSize());

		// As there could be many instances access at a same time, checking request process must be atomic. Transaction can't be used as
		// the process include get and then check process and get inside a transaction would return null for all cases. Therefore, lua
		// script or distributed lock (SETNX) must be used. In this case, we chose lua script.
		return Boolean.TRUE.equals(redisTemplate.execute(RedisScript.of(luaScriptResource, Boolean.class), keys,
														 args.toArray(new Object[0])));
	}
}
