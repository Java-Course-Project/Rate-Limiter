package org.example.ratelimiter.config;

import lombok.RequiredArgsConstructor;
import org.example.ratelimiter.limiter.RateLimiterHandlerInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class RateLimiterConfiguration implements WebMvcConfigurer {
	private final RateLimiterHandlerInterceptor interceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(interceptor)
				.addPathPatterns("/**");
	}
}
