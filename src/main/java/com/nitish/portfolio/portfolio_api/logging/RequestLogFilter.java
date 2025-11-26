// package: com.nitish.portfolio.portfolio_api.logging

package com.nitish.portfolio.portfolio_api.logging;

import com.nitish.portfolio.portfolio_api.model.RequestLog;
import com.nitish.portfolio.portfolio_api.repository.RequestLogRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RequestLogFilter extends OncePerRequestFilter {

    private final RequestLogRepository logRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // Optional: skip logging for admin log endpoint itself & health
        if (uri.startsWith("/api/admin/logs") || uri.startsWith("/api/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = getClientIp(request);
        String ua = request.getHeader("User-Agent");

        RequestLog log = RequestLog.builder()
                .ipAddress(ip)
                .userAgent(ua)
                .deviceType(detectDeviceType(ua))
                .browser(detectBrowser(ua))
                .build();

        logRepository.save(log);

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String header = request.getHeader("X-Forwarded-For");
        if (header != null && !header.isBlank()) {
            return header.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String detectDeviceType(String ua) {
        if (ua == null) return "Unknown";
        ua = ua.toLowerCase();
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) {
            return "Mobile";
        }
        if (ua.contains("tablet") || ua.contains("ipad")) {
            return "Tablet";
        }
        return "Desktop";
    }

    private String detectBrowser(String ua) {
        if (ua == null) return "Unknown";
        ua = ua.toLowerCase();
        if (ua.contains("edg")) return "Edge";
        if (ua.contains("chrome")) return "Chrome";
        if (ua.contains("firefox")) return "Firefox";
        if (ua.contains("safari")) return "Safari";
        if (ua.contains("opera") || ua.contains("opr")) return "Opera";
        return "Other";
    }
}
