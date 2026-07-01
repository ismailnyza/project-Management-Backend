package com.pm.workflow;

import com.pm.project.Project;
import com.pm.project.ProjectRepository;
import com.pm.workflow.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkflowService {
    private final WorkflowTransitionRepository transitionRepo;
    private final ProjectRepository projectRepo;

    /**
     * Seed default workflow for a new project:
     * TODO → IN_PROGRESS → IN_REVIEW → DONE
     * with transitions back from any status.
     */
    public void seedDefaults(Long projectId) {
        Project project = projectRepo.getReferenceById(projectId);
        add(project, "TODO", "IN_PROGRESS", "Start Work");
        add(project, "IN_PROGRESS", "IN_REVIEW", "Submit Review");
        add(project, "IN_REVIEW", "DONE", "Approve");
        add(project, "IN_REVIEW", "IN_PROGRESS", "Reject");
        // Allow going back to TODO from any status
        add(project, "IN_PROGRESS", "TODO", "Move Back");
        add(project, "IN_REVIEW", "TODO", "Move Back");
        add(project, "DONE", "TODO", "Reopen");
    }

    @Transactional(readOnly = true)
    public List<TransitionDto> findByProject(Long projectId) {
        return transitionRepo.findByProjectIdOrderByIdAsc(projectId).stream()
            .map(t -> new TransitionDto(t.getId(), t.getFromStatus(), t.getToStatus(), t.getName()))
            .toList();
    }

    public TransitionDto addTransition(Long projectId, CreateTransitionRequest req) {
        if (transitionRepo.existsByProjectIdAndFromStatusAndToStatus(projectId, req.fromStatus(), req.toStatus())) {
            throw new IllegalArgumentException("Transition already exists");
        }
        Project project = projectRepo.getReferenceById(projectId);
        WorkflowTransition t = new WorkflowTransition();
        t.setProject(project);
        t.setFromStatus(req.fromStatus());
        t.setToStatus(req.toStatus());
        t.setName(req.name());
        t = transitionRepo.save(t);
        return new TransitionDto(t.getId(), t.getFromStatus(), t.getToStatus(), t.getName());
    }

    public void deleteTransition(Long transitionId) {
        transitionRepo.deleteById(transitionId);
    }

    /**
     * The core workflow validation: does this transition exist for this project?
     * Throws IllegalStateException if not allowed.
     */
    public void validateTransition(Long projectId, String fromStatus, String toStatus) {
        if (fromStatus.equals(toStatus)) return; // no-op transition is always allowed
        transitionRepo.findByProjectIdAndFromStatusAndToStatus(projectId, fromStatus, toStatus)
            .orElseThrow(() -> new IllegalStateException(
                "Transition not allowed: " + fromStatus + " → " + toStatus));
    }

    private void add(Project project, String from, String to, String name) {
        WorkflowTransition t = new WorkflowTransition();
        t.setProject(project);
        t.setFromStatus(from);
        t.setToStatus(to);
        t.setName(name);
        transitionRepo.save(t);
    }
}
