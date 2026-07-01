package com.pm.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter implements Filter {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var res = (HttpServletResponse) response;

        String path = ((jakarta.servlet.http.HttpServletRequest) request).getRequestURI();
        // Only rate-limit auth endpoints
        if (!path.startsWith("/api/v1/auth/")) {
            chain.doFilter(request, response);
            return;
        }

        String clientIp = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(clientIp, k -> Bucket.builder()
            .addLimit(Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1))))
            .build());

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            res.setContentType("application/json");
            res.setStatus(429);
            res.getWriter().write("{\"error\":\"Too many requests. Try again later.\",\"status\":429}");
        }
    }
}
