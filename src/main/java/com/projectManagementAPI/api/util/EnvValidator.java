// java, update `src/main/java/com/projectManagementAPI/api/util/EnvValidator.java`
package com.projectManagementAPI.api.util;

import com.projectManagementAPI.api.error.StartupException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class EnvValidator {
    public static void ensure(String... names) throws StartupException {
        List<String> missing = Arrays.stream(names)
                .filter(n -> {
                    String v = System.getenv(n);
                    if (v != null && !v.isEmpty()) return false;
                    // fallback to system property loaded from .env
                    String pv = System.getProperty(n);
                    return pv == null || pv.isEmpty();
                })
                .collect(Collectors.toList());

        if (!missing.isEmpty()) {
            throw new StartupException("Missing environment variables: " + String.join(", ", missing));
        }
    }
}
