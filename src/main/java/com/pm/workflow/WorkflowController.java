package com.pm.workflow;

import com.pm.workflow.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/workflow")
@RequiredArgsConstructor
public class WorkflowController {
    private final WorkflowService workflowService;

    @GetMapping("/transitions")
    public ResponseEntity<List<TransitionDto>> listTransitions(@PathVariable Long projectId) {
        return ResponseEntity.ok(workflowService.findByProject(projectId));
    }

    @PostMapping("/transitions")
    public ResponseEntity<TransitionDto> addTransition(@PathVariable Long projectId,
                                                        @RequestBody CreateTransitionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(workflowService.addTransition(projectId, req));
    }

    @DeleteMapping("/transitions/{transitionId}")
    public ResponseEntity<Void> deleteTransition(@PathVariable Long transitionId) {
        workflowService.deleteTransition(transitionId);
        return ResponseEntity.noContent().build();
    }
}
