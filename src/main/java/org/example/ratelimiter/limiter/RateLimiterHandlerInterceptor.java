package org.example.ratelimiter.limiter;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.ratelimiter.annotation.TokenSize;
import org.example.ratelimiter.service.RateLimiterService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class RateLimiterHandlerInterceptor implements HandlerInterceptor {
	private final RateLimiterService rateLimiterService;

	@Override
	public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler)
			throws Exception {

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

		response.setContentType("application/json");
		response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
		response.getWriter().write("{\"error\":\"Too Many Requests\"}");

		return false;
	}
}
