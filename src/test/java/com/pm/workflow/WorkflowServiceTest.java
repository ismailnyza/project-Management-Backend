package com.pm.workflow;

import com.pm.workflow.dto.CreateTransitionRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkflowServiceTest {
    @Mock private WorkflowTransitionRepository transitionRepo;
    @Mock private com.pm.project.ProjectRepository projectRepo;
    @InjectMocks private WorkflowService workflowService;

    @Test
    void validateTransitionShouldAllowDefinedTransition() {
        when(transitionRepo.findByProjectIdAndFromStatusAndToStatus(1L, "TODO", "IN_PROGRESS"))
            .thenReturn(Optional.of(new WorkflowTransition()));
        assertDoesNotThrow(() -> workflowService.validateTransition(1L, "TODO", "IN_PROGRESS"));
    }

    @Test
    void validateTransitionShouldRejectUndefinedTransition() {
        when(transitionRepo.findByProjectIdAndFromStatusAndToStatus(1L, "TODO", "DONE"))
            .thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class,
            () -> workflowService.validateTransition(1L, "TODO", "DONE"));
    }

    @Test
    void validateTransitionShouldAllowSameStatus() {
        assertDoesNotThrow(() -> workflowService.validateTransition(1L, "TODO", "TODO"));
    }

    @Test
    void addTransitionShouldRejectDuplicate() {
        when(transitionRepo.existsByProjectIdAndFromStatusAndToStatus(1L, "A", "B"))
            .thenReturn(true);
        assertThrows(IllegalArgumentException.class,
            () -> workflowService.addTransition(1L, new CreateTransitionRequest("A", "B", "Test")));
    }
}
