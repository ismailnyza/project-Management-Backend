package com.pm.sprint;

import com.pm.project.Project;
import com.pm.project.ProjectRepository;
import com.pm.sprint.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SprintService {
    private final SprintRepository sprintRepo;
    private final ProjectRepository projectRepo;

    public SprintDto create(Long projectId, CreateSprintRequest req) {
        Project project = projectRepo.getReferenceById(projectId);
        Sprint s = new Sprint();
        s.setProject(project);
        s.setName(req.name());
        s.setGoal(req.goal() != null ? req.goal() : "");
        s.setStartDate(req.startDate());
        s.setEndDate(req.endDate());
        s.setActive(true);
        return toDto(sprintRepo.save(s));
    }

    @Transactional(readOnly = true)
    public List<SprintDto> findByProject(Long projectId) {
        return sprintRepo.findByProjectIdOrderByIdDesc(projectId).stream()
            .map(this::toDto).toList();
    }

    public SprintDto update(Long id, CreateSprintRequest req) {
        Sprint s = sprintRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Sprint not found"));
        if (req.name() != null) s.setName(req.name());
        if (req.goal() != null) s.setGoal(req.goal());
        if (req.startDate() != null) s.setStartDate(req.startDate());
        if (req.endDate() != null) s.setEndDate(req.endDate());
        return toDto(sprintRepo.save(s));
    }

    private SprintDto toDto(Sprint s) {
        return new SprintDto(s.getId(), s.getProject().getId(), s.getName(), s.getGoal(),
            s.getStartDate(), s.getEndDate(), s.isActive());
    }
}
