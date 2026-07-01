package com.pm.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test that exercises the full API against the configured database.
 * When Docker is available, extend BaseIntegrationTest to run against a real PostgreSQL
 * via Testcontainers instead of H2.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PostgresIntegrationTest {
    @LocalServerPort private int port;
    @Autowired private TestRestTemplate rest;

    private String url(String path) { return "http://localhost:" + port + path; }

    private HttpHeaders authHeader(String token) {
        var h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.setBearerAuth(token);
        return h;
    }

    @Test
    void fullApiFlow() {
        // Register
        var reg = rest.postForEntity(url("/api/v1/auth/register"),
            Map.of("name", "Test User", "email", "flow@test.com", "password", "test123"), Map.class);
        assertEquals(HttpStatus.CREATED, reg.getStatusCode());
        String token = (String) reg.getBody().get("token");
        assertNotNull(token, "Token should be present in register response");
        assertNotNull(reg.getBody().get("refreshToken"), "Refresh token should be present");

        // Create project
        var proj = rest.exchange(url("/api/v1/projects"), HttpMethod.POST,
            new HttpEntity<>(Map.of("name", "Demo Project", "key", "DEMO"), authHeader(token)), Map.class);
        assertEquals(HttpStatus.CREATED, proj.getStatusCode());
        long pid = ((Number) proj.getBody().get("id")).longValue();

        // Create issue
        var issue = rest.exchange(url("/api/v1/projects/" + pid + "/issues"), HttpMethod.POST,
            new HttpEntity<>(Map.of("type", "TASK", "title", "Test issue", "priority", "HIGH"),
                authHeader(token)), Map.class);
        assertEquals(HttpStatus.CREATED, issue.getStatusCode());
        long iid = ((Number) issue.getBody().get("id")).longValue();

        // Get issue detail
        var detail = rest.exchange(url("/api/v1/issues/" + iid), HttpMethod.GET,
            new HttpEntity<>(authHeader(token)), Map.class);
        assertEquals(HttpStatus.OK, detail.getStatusCode());
        var issueData = (Map<String, Object>) detail.getBody().get("issue");
        assertEquals("Test issue", issueData.get("title"));
        assertEquals("HIGH", issueData.get("priority"));

        // Verify workflow transitions exist
        var wf = rest.exchange(url("/api/v1/projects/" + pid + "/workflow/transitions"), HttpMethod.GET,
            new HttpEntity<>(authHeader(token)), java.util.List.class);
        assertEquals(HttpStatus.OK, wf.getStatusCode());
        assertFalse(wf.getBody().isEmpty(), "Project should have default workflow transitions");

        // Verify user list
        var users = rest.exchange(url("/api/v1/users"), HttpMethod.GET,
            new HttpEntity<>(authHeader(token)), java.util.List.class);
        assertEquals(HttpStatus.OK, users.getStatusCode());
        assertFalse(users.getBody().isEmpty());
    }
}
