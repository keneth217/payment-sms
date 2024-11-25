package com.payment.paystack.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCorsFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(SimpleCorsFilter.class);
    private static final List<String> ALLOWED_ORIGINS = List.of(
            "https://shop-backend-iwr1.onrender.com",
            "https://shop-front-sepia.vercel.app",
            "http://localhost:8080",
            "http://localhost:4200"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String originHeader = request.getHeader("Origin");
        logger.info("Origin Header: {}", originHeader);

        if (originHeader != null && ALLOWED_ORIGINS.contains(originHeader)) {
            response.setHeader("Access-Control-Allow-Origin", originHeader);
        } else {
            logger.warn("Origin not allowed: {}", originHeader);
        }

        // Adding CORS headers
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With, Accept, Origin");

        // Allow credentials if needed
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // Handle OPTIONS preflight request
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return; // Immediately return for preflight
        }

        // Continue with the request
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // No-op
    }

    @Override
    public void destroy() {
        // No-op
    }
}