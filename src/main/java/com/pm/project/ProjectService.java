package com.pm.project;

import com.pm.project.dto.*;
import com.pm.user.User;
import com.pm.user.UserRepository;
import com.pm.workflow.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {
    private final ProjectRepository projectRepo;
    private final UserRepository userRepo;
    private final WorkflowService workflowService;

    public ProjectDto create(CreateProjectRequest req, Long ownerId) {
        if (projectRepo.existsByKey(req.key())) {
            throw new IllegalArgumentException("Project key already taken: " + req.key());
        }
        User owner = userRepo.findById(ownerId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Project p = new Project();
        p.setName(req.name());
        p.setKey(req.key().toUpperCase());
        p.setDescription(req.description() != null ? req.description() : "");
        p.setOwner(owner);
        p = projectRepo.save(p);

        // Ponytail: seed default workflow transitions
        workflowService.seedDefaults(p.getId());

        return toDto(p);
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> findAll() {
        return projectRepo.findAllByOrderByIdAsc().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ProjectDto findById(Long id) {
        return projectRepo.findById(id).map(this::toDto)
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
    }

    @Transactional(readOnly = true)
    public ProjectDetailDto findDetail(Long id) {
        Project p = projectRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
        return new ProjectDetailDto(p.getId(), p.getName(), p.getKey(),
            p.getDescription(), p.getOwner().getId(), workflowService.findByProject(id));
    }

    public void delete(Long id) {
        projectRepo.deleteById(id);
    }

    private ProjectDto toDto(Project p) {
        return new ProjectDto(p.getId(), p.getName(), p.getKey(), p.getDescription(), p.getOwner().getId());
    }
}
