package com.pm.common;

import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Map;

public class AuthHelper {
    public static Long getUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @SuppressWarnings("unchecked")
    public static String getRole() {
        var details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        if (details instanceof Map) return (String) ((Map<?, ?>) details).get("role");
        return "MEMBER";
    }

    public static void requireAdmin() {
        if (!"ADMIN".equals(getRole())) {
            throw new org.springframework.security.access.AccessDeniedException("Admin role required");
        }
    }
}
