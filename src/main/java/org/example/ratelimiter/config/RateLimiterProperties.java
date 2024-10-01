package org.example.ratelimiter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "application.rate-limiter")
@Data
public class RateLimiterProperties {
	private long bucketSize;
	private Duration refillRate;
	private long bucketCapacity;
}
