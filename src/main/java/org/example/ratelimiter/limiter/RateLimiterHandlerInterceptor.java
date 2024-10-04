package org.example.ratelimiter.limiter;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ratelimiter.annotation.TokenSize;
import org.example.ratelimiter.exception.TooManyRequestException;
import org.example.ratelimiter.service.RateLimiterService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
@Slf4j
public class RateLimiterHandlerInterceptor implements HandlerInterceptor {
	private final RateLimiterService rateLimiterService;

	@Override
	public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
		int token = 1;
		if (handler instanceof HandlerMethod handlerMethod) {
			TokenSize tokenSize = handlerMethod.getMethodAnnotation(TokenSize.class);
			if (tokenSize != null) {
				token = tokenSize.token();
			}
		}

		boolean isSuccessPassRateLimiter = rateLimiterService.tryAcquire(token);
		if (isSuccessPassRateLimiter) {
			return true;
		}

		throw new TooManyRequestException();
	}
}
