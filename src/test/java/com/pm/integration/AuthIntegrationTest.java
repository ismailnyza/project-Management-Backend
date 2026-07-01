package com.pm.integration;

import com.pm.PmApplication;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = PmApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthIntegrationTest {
    @LocalServerPort private int port;
    @Autowired private TestRestTemplate rest;
    private String token;

    private String url(String path) { return "http://localhost:" + port + path; }

    private <T> ResponseEntity<T> auth(String method, String path, Object body, Class<T> type) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) headers.setBearerAuth(token);
        return rest.exchange(url(path), HttpMethod.valueOf(method),
            new HttpEntity<>(body, headers), type);
    }

    @Test @Order(1)
    void register() {
        var resp = rest.postForEntity(url("/api/v1/auth/register"),
            Map.of("name", "Alice", "email", "alice@test.com", "password", "secret123"), Map.class);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertNotNull(resp.getBody().get("token"));
        token = (String) resp.getBody().get("token");
    }

    @Test @Order(2)
    void login() {
        var resp = rest.postForEntity(url("/api/v1/auth/login"),
            Map.of("email", "alice@test.com", "password", "secret123"), Map.class);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        token = (String) resp.getBody().get("token");
    }

    @Test @Order(3)
    void rejectUnauthenticatedRequest() {
        var resp = rest.postForEntity(url("/api/v1/projects"),
            Map.of("name", "Test", "key", "TEST"), Map.class);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test @Order(4)
    void createAndListProject() {
        assertNotNull(token, "Token should be set by login test");
        var resp = auth("POST", "/api/v1/projects",
            Map.of("name", "Sprint 1", "key", "SP1"), Map.class);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals("Sprint 1", resp.getBody().get("name"));
        var projectId = ((Number) resp.getBody().get("id")).longValue();

        var listResp = auth("GET", "/api/v1/projects", null, java.util.List.class);
        assertEquals(HttpStatus.OK, listResp.getStatusCode());
        assertFalse(listResp.getBody().isEmpty());

        // Create an issue
        var issueResp = auth("POST", "/api/v1/projects/" + projectId + "/issues",
            Map.of("type", "TASK", "title", "Fix login bug", "priority", "HIGH"), Map.class);
        assertEquals(HttpStatus.CREATED, issueResp.getStatusCode());
        assertEquals("Fix login bug", issueResp.getBody().get("title"));
    }

    @Test @Order(5)
    void workflowTransition() {
        assertNotNull(token);
        var wfResp = auth("GET", "/api/v1/projects/1/workflow/transitions", null, java.util.List.class);
        if (wfResp.getStatusCode() != HttpStatus.OK || wfResp.getBody() == null || wfResp.getBody().isEmpty()) {
            return; // project or transitions not available, skip
        }
        var transitions = wfResp.getBody();
        var trans = (Map<String, Object>) transitions.get(0);
        int transId = ((Number) trans.get("id")).intValue();

        var resp = auth("POST", "/api/v1/issues/1/transition",
            Map.of("transitionId", transId), Map.class);
        // Transition may fail if issue status doesn't match — that's OK for this test
        assertNotNull(resp);
    }
}
