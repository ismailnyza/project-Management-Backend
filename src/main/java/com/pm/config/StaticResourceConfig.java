package com.pm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import java.io.IOException;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve built frontend from /app/static (Docker) or classpath
        registry.addResourceHandler("/**")
            .addResourceLocations("file:/app/static/", "classpath:/static/")
            .resourceChain(true)
            .addResolver(new PathResourceResolver() {
                @Override
                protected Resource getResource(String resourcePath, Resource location) throws IOException {
                    Resource resource = location.createRelative(resourcePath);
                    if (resource.exists() && resource.isReadable()) return resource;
                    // SPA fallback: return index.html for non-file paths
                    if (!resourcePath.startsWith("api/")) {
                        Resource fallback = location.createRelative("index.html");
                        if (fallback.exists() && fallback.isReadable()) return fallback;
                    }
                    return null;
                }
            });
    }
}
