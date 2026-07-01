package com.pm.issue;

import com.pm.common.PageResponse;
import com.pm.issue.dto.*;
import com.pm.issue.enums.IssueType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.pm.common.AuthHelper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class IssueController {
    private final IssueService issueService;

    private Long getUserId() { return AuthHelper.getUserId(); }

    @PostMapping("/projects/{projectId}/issues")
    public ResponseEntity<IssueDto> create(@PathVariable Long projectId,
                                           @Valid @RequestBody CreateIssueRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(issueService.create(projectId, req, getUserId()));
    }

    @GetMapping("/projects/{projectId}/issues")
    public ResponseEntity<PageResponse<IssueDto>> listBoard(
            @PathVariable Long projectId,
            @RequestParam(required = false) IssueType type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(issueService.findBoardIssues(projectId, type, status, assigneeId, search, page, size));
    }

    @GetMapping("/issues/{id}")
    public ResponseEntity<IssueDetailDto> getIssue(@PathVariable Long id) {
        return ResponseEntity.ok(issueService.findById(id));
    }

    @PatchMapping("/issues/{id}")
    public ResponseEntity<IssueDto> update(@PathVariable Long id,
                                           @Valid @RequestBody UpdateIssueRequest req) {
        return ResponseEntity.ok(issueService.update(id, req, getUserId()));
    }

    @PostMapping("/issues/{id}/transition")
    public ResponseEntity<IssueDto> transition(@PathVariable Long id,
                                               @RequestBody TransitionRequest req) {
        return ResponseEntity.ok(issueService.transition(id, req.transitionId(), getUserId()));
    }

    @DeleteMapping("/issues/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        AuthHelper.requireAdmin();
        issueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
