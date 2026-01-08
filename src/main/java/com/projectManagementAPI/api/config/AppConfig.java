package com.projectManagementAPI.api.config;

public final class AppConfig {

    private final int port;
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    private AppConfig(int port, String dbUrl, String dbUser, String dbPassword) {
        this.port = port;
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    public static AppConfig load() {
        return new AppConfig(
                getEnvInt("APP_PORT", 8000),
                getRequiredEnv("DATABASE_URL"),
                getRequiredEnv("DATABASE_USER"),
                getRequiredEnv("DATABASE_PASSWORD")
        );
    }

    private static String getRequiredEnv(String name) {
        String v = System.getenv(name);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException("Missing required env var: " + name);
        }
        return v;
    }

    private static int getEnvInt(String name, int defaultValue) {
        String v = System.getenv(name);
        if (v == null || v.isBlank()) return defaultValue;
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalStateException(
                    "Invalid integer for env var " + name + ": " + v
            );
        }
    }

    public int getPort() { return port; }
    public String getDbUrl() { return dbUrl; }
    public String getDbUser() { return dbUser; }
    public String getDbPassword() { return dbPassword; }
}
