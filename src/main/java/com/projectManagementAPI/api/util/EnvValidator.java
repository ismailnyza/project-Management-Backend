package com.projectManagementAPI.api.util;

import com.projectManagementAPI.api.error.StartupException;

import java.util.ArrayList;
import java.util.List;

public final class EnvValidator {
    private EnvValidator() {}

    public static void ensure(String... requiredVars) {
        List<String> missing = new ArrayList<>();
        for (String v : requiredVars) {
            if (System.getenv(v) == null || System.getenv(v).isBlank()) {
                missing.add(v);
            }
        }
        if (!missing.isEmpty()) {
            throw new StartupException("Missing required environment variables: " + String.join(", ", missing));
        }
    }
}
