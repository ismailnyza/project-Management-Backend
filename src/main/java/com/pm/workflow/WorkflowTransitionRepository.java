package com.pm.workflow;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WorkflowTransitionRepository extends JpaRepository<WorkflowTransition, Long> {
    List<WorkflowTransition> findByProjectIdOrderByIdAsc(Long projectId);
    Optional<WorkflowTransition> findByProjectIdAndFromStatusAndToStatus(Long projectId, String fromStatus, String toStatus);
    boolean existsByProjectIdAndFromStatusAndToStatus(Long projectId, String fromStatus, String toStatus);
}
