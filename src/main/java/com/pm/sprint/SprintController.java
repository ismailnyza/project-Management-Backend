package com.pm.sprint;

import com.pm.sprint.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/sprints")
@RequiredArgsConstructor
public class SprintController {
    private final SprintService sprintService;

    @PostMapping
    public ResponseEntity<SprintDto> create(@PathVariable Long projectId,
                                            @RequestBody CreateSprintRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sprintService.create(projectId, req));
    }

    @GetMapping
    public ResponseEntity<List<SprintDto>> listByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(sprintService.findByProject(projectId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SprintDto> update(@PathVariable Long id,
                                            @RequestBody CreateSprintRequest req) {
        return ResponseEntity.ok(sprintService.update(id, req));
    }
}
