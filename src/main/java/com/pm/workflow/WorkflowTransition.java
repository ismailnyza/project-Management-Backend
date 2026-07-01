package com.pm.workflow;

import com.pm.common.BaseEntity;
import com.pm.project.Project;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "workflow_transitions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"project_id", "from_status", "to_status"})
})
@Getter @Setter @NoArgsConstructor
public class WorkflowTransition extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "from_status", nullable = false, length = 50)
    private String fromStatus;

    @Column(name = "to_status", nullable = false, length = 50)
    private String toStatus;

    @Column(nullable = false, length = 100)
    private String name;
}
