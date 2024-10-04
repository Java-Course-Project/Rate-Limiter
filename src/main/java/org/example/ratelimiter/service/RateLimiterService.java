package org.example.ratelimiter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ratelimiter.config.RateLimiterProperties;
import org.example.ratelimiter.model.TokenBucket;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimiterService {
	private final RateLimiterProperties rateLimiterProperties;
	private final RedisTemplate<String, TokenBucket> redisTemplate;
	private static final String RATE_LIMITER_KEY = "rate-limiter";

	public boolean tryAcquire(int token) {
		TokenBucket tokenBucket = redisTemplate.opsForValue().get(RATE_LIMITER_KEY);
		if (tokenBucket == null) {
			tokenBucket =
					TokenBucket.builder().lastRefillTimestamp(System.currentTimeMillis()).tokens(rateLimiterProperties.getBucketSize())
							   .build();
		}

		long now = System.currentTimeMillis();
		long tokensToFill = (now - tokenBucket.getLastRefillTimestamp()) / rateLimiterProperties.getRefillRate().toMillis();
		if (tokensToFill > 0) {
			tokenBucket.setLastRefillTimestamp(
					tokenBucket.getLastRefillTimestamp() + rateLimiterProperties.getRefillRate().toMillis() * tokensToFill);
			tokenBucket.setTokens(tokenBucket.getTokens() + tokensToFill);
		}

		if (tokenBucket.getTokens() >= rateLimiterProperties.getBucketCapacity()) {
			tokenBucket.setTokens(rateLimiterProperties.getBucketCapacity());
		}

		log.debug("Current tokens: {}. Tokens needed: {}", tokenBucket.getTokens(), token);
		if (tokenBucket.getTokens() < token) {
			redisTemplate.opsForValue().set(RATE_LIMITER_KEY, tokenBucket);
			return false;
		}
		tokenBucket.setTokens(tokenBucket.getTokens() - token);
		redisTemplate.opsForValue().set(RATE_LIMITER_KEY, tokenBucket);
		return true;
	}
}
