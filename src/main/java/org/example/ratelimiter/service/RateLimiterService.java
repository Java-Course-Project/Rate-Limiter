package org.example.ratelimiter.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ratelimiter.config.RateLimiterProperties;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimiterService {
	private final RateLimiterProperties rateLimiterProperties;

	// TODO: should be on Redis
	private long tokens;
	private volatile long lastRefillTimestamp;

	@PostConstruct
	public void init() {
		tokens = rateLimiterProperties.getBucketSize();
		lastRefillTimestamp = System.currentTimeMillis();
	}

	public synchronized boolean tryAcquire(int token) {
		long now = System.currentTimeMillis();
		long tokensToFill = (now - lastRefillTimestamp) / rateLimiterProperties.getRefillRate().toMillis();
		if (tokensToFill > 0) {
			lastRefillTimestamp += rateLimiterProperties.getRefillRate().toMillis() * tokensToFill;
			tokens += tokensToFill;
		}

		if (tokens >= rateLimiterProperties.getBucketCapacity()) {
			tokens = rateLimiterProperties.getBucketCapacity();
		}

		if (tokens < token) {
			return false;
		}
		tokens -= token;
		return true;
	}
}
