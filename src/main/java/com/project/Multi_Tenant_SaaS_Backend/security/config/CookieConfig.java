package com.project.Multi_Tenant_SaaS_Backend.security.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CookieConfig {
    @Value("${app.cookie.secure:true}")
    private boolean cookieSecure;

    @Value("${app.cookie.domain:}")
    private String cookieDomain;

    public void addSecureCookie(HttpServletResponse response, String name, String value, int maxAge, String path) {
        // Use ResponseCookie for better control
        String cookie = String.format(
                "%s=%s; Path=%s; Max-Age=%d; HttpOnly; Secure; SameSite=None",
                name, value, path, maxAge
        );

        // Add domain if specified (for cross-domain cookies)
        if (cookieDomain != null && !cookieDomain.isEmpty()) {
            cookie += "; Domain=" + cookieDomain;
        }

        response.addHeader("Set-Cookie", cookie);
    }
}
