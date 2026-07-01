package com.pm.config;

import com.pm.project.Project;
import com.pm.project.ProjectRepository;
import com.pm.user.Role;
import com.pm.user.User;
import com.pm.user.UserRepository;
import com.pm.issue.Issue;
import com.pm.issue.IssueRepository;
import com.pm.issue.enums.IssueType;
import com.pm.issue.enums.Priority;
import com.pm.workflow.WorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class SeedDataRunner implements CommandLineRunner {
    private final UserRepository userRepo;
    private final ProjectRepository projectRepo;
    private final IssueRepository issueRepo;
    private final PasswordEncoder passwordEncoder;
    private final WorkflowService workflowService;

    @Override
    public void run(String... args) {
        if (userRepo.count() > 0) {
            log.info("Data already exists, skipping seed");
            return;
        }
        log.info("Seeding demo data...");

        // Users
        User alice = createUser("Alice Chen", "alice@demo.com", "demo123");
        User bob = createUser("Bob Smith", "bob@demo.com", "demo123");
        User carol = createUser("Carol Davis", "carol@demo.com", "demo123");

        // Project
        Project p = new Project();
        p.setName("Platform Redesign");
        p.setKey("PLAT");
        p.setDescription("Q3 initiative to redesign the core platform");
        p.setOwner(alice);
        p = projectRepo.save(p);
        workflowService.seedDefaults(p.getId());

        // Issues
        Issue epic = createIssue(p, IssueType.EPIC, "User Dashboard v2", Priority.HIGH,
            "Redesign the main user dashboard with new analytics widgets", alice, bob, "IN_PROGRESS", null);
        Issue story1 = createIssue(p, IssueType.STORY, "Revenue chart widget", Priority.HIGH,
            "Add a real-time revenue chart using the new billing API", alice, bob, "IN_PROGRESS", epic);
        Issue story2 = createIssue(p, IssueType.STORY, "User activity feed", Priority.MEDIUM,
            "Show recent user actions in a scrollable feed", alice, carol, "TODO", epic);
        Issue subtask1 = createIssue(p, IssueType.SUBTASK, "Design chart API contract", Priority.HIGH,
            "Define OpenAPI spec for chart data endpoint", alice, bob, "DONE", story1);
        Issue subtask2 = createIssue(p, IssueType.SUBTASK, "Implement chart rendering", Priority.HIGH,
            "Use D3.js for animated bar charts", alice, bob, "IN_PROGRESS", story1);
        Issue bug = createIssue(p, IssueType.BUG, "Dashboard 500 error on empty state", Priority.CRITICAL,
            "GET /api/dashboard returns 500 when user has no data", alice, carol, "TODO", null);
        Issue task = createIssue(p, IssueType.TASK, "Write integration tests", Priority.MEDIUM,
            "Add Testcontainers integration tests for the dashboard module", alice, null, "TODO", null);

        log.info("Seeded {} users, 1 project, {} issues", 3, 7);
    }

    private User createUser(String name, String email, String password) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(password));
        if (userRepo.count() == 0) u.setRole(Role.ADMIN);
        return userRepo.save(u);
    }

    private Issue createIssue(Project p, IssueType type, String title, Priority prio,
                               String desc, User reporter, User assignee, String status, Issue parent) {
        Issue i = new Issue();
        i.setProject(p);
        i.setType(type);
        i.setTitle(title);
        i.setDescription(desc);
        i.setPriority(prio);
        i.setReporter(reporter);
        i.setAssignee(assignee);
        i.setStatus(status);
        i.setParent(parent);
        return issueRepo.save(i);
    }
}
