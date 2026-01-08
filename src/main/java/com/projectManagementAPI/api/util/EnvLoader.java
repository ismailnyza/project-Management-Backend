package com.projectManagementAPI.api.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public final class EnvLoader {
    public static void loadDotEnv(String path) {
        try (Stream<String> lines = Files.lines(Paths.get(path))) {
            lines.map(String::trim)
                    .filter(l -> !l.isEmpty() && !l.startsWith("#"))
                    .forEach(l -> {
                        String[] parts = l.split("=", 2);
                        if (parts.length >= 1) {
                            String k = parts[0].trim();
                            String v = parts.length == 2 ? parts[1].trim() : "";
                            // set as system property for fallback
                            System.setProperty(k, v);
                        }
                    });
        } catch (IOException ignored) {
            // ignore if .env missing; caller can handle missing envs via EnvValidator
        }
    }
}
