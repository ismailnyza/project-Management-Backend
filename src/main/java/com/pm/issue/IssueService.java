package com.pm.issue;

import com.pm.activity.ActivityLogService;
import com.pm.comment.CommentDto;
import com.pm.common.PageResponse;
import com.pm.issue.dto.*;
import com.pm.issue.enums.IssueType;
import com.pm.project.ProjectRepository;
import com.pm.sprint.SprintRepository;
import com.pm.user.UserRepository;
import com.pm.workflow.WorkflowService;
import com.pm.workflow.WorkflowTransition;
import com.pm.workflow.WorkflowTransitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class IssueService {
    private final IssueRepository issueRepo;
    private final ProjectRepository projectRepo;
    private final UserRepository userRepo;
    private final SprintRepository sprintRepo;
    private final WorkflowService workflowService;
    private final WorkflowTransitionRepository transitionRepo;
    private final ActivityLogService activityLog;

    public IssueDto create(Long projectId, CreateIssueRequest req, Long reporterId) {
        var project = projectRepo.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        var reporter = userRepo.getReferenceById(reporterId);

        Issue issue = new Issue();
        issue.setProject(project);
        issue.setType(req.type());
        issue.setTitle(req.title());
        issue.setDescription(req.description() != null ? req.description() : "");
        issue.setPriority(req.priority() != null ? req.priority() : null);
        issue.setStatus(req.status() != null ? req.status() : "TODO");
        issue.setReporter(reporter);
        issue.setStoryPoints(req.storyPoints());
        issue.setDueDate(req.dueDate());

        if (req.assigneeId() != null)
            issue.setAssignee(userRepo.getReferenceById(req.assigneeId()));
        if (req.parentId() != null) {
            Issue parent = issueRepo.findById(req.parentId())
                .orElseThrow(() -> new IllegalArgumentException("Parent issue not found"));
            if (!parent.getProject().getId().equals(projectId))
                throw new IllegalArgumentException("Parent must be in the same project");
            issue.setParent(parent);
            // ponytail: child of an epic becomes STORY, child of story/task becomes SUBTASK
            if (issue.getType() == null || issue.getType() == IssueType.TASK) {
                issue.setType(parent.isEpic() ? IssueType.STORY : IssueType.SUBTASK);
            }
        }
        if (req.sprintId() != null)
            issue.setSprint(sprintRepo.getReferenceById(req.sprintId()));

        return toDto(issueRepo.save(issue));
    }

    @Transactional(readOnly = true)
    public PageResponse<IssueDto> findBoardIssues(Long projectId, IssueType type, String status,
                                                   Long assigneeId, String search, int page, int size) {
        var pageResult = issueRepo.findBoardIssues(projectId, type, status, assigneeId, search,
            PageRequest.of(page - 1, size));
        return PageResponse.of(
            pageResult.getContent().stream().map(this::toDto).toList(),
            page, size, pageResult.getTotalElements());
    }

    @Transactional(readOnly = true)
    public IssueDetailDto findById(Long id) {
        Issue issue = issueRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Issue not found: " + id));
        List<IssueDto> subtasks = issueRepo.findSubtasks(id).stream()
            .map(this::toDto).toList();
        List<CommentDto> comments = issue.getComments().stream()
            .map(c -> new CommentDto(c.getId(), c.getIssue().getId(), c.getUser().getId(),
                c.getUser().getName(), c.getBody(), c.getCreatedAt())).toList();
        return new IssueDetailDto(toDto(issue), subtasks, comments);
    }

    public IssueDto update(Long id, UpdateIssueRequest req, Long userId) {
        Issue issue = issueRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Issue not found: " + id));

        if (req.title() != null) {
            activityLog.log(issue, userId, "title", issue.getTitle(), req.title());
            issue.setTitle(req.title());
        }
        if (req.description() != null) issue.setDescription(req.description());
        if (req.priority() != null) issue.setPriority(req.priority());
        if (req.assigneeId() != null) {
            String old = issue.getAssignee() != null ? issue.getAssignee().getName() : "unassigned";
            issue.setAssignee(userRepo.getReferenceById(req.assigneeId()));
            activityLog.log(issue, userId, "assignee", old,
                issue.getAssignee().getName());
        }
        if (req.sprintId() != null)
            issue.setSprint(sprintRepo.getReferenceById(req.sprintId()));
        if (req.storyPoints() != null) issue.setStoryPoints(req.storyPoints());
        if (req.dueDate() != null) issue.setDueDate(req.dueDate());

        return toDto(issueRepo.save(issue));
    }

    /**
     * The crown jewel: transition an issue through the workflow.
     * Validates the transition exists for this project's workflow.
     */
    public IssueDto transition(Long id, Long transitionId, Long userId) {
        Issue issue = issueRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Issue not found: " + id));
        WorkflowTransition transition = transitionRepo.findById(transitionId)
            .orElseThrow(() -> new IllegalArgumentException("Transition not found"));

        if (!transition.getProject().getId().equals(issue.getProject().getId()))
            throw new IllegalArgumentException("Transition does not belong to this project");

        // Business rule: can't close a story/task/epic if it has open subtasks
        if ("DONE".equals(transition.getToStatus()) && !issue.isSubtask()) {
            long openSubtasks = issueRepo.findSubtasks(id).stream()
                .filter(s -> !"DONE".equals(s.getStatus())).count();
            if (openSubtasks > 0)
                throw new IllegalStateException(
                    "Cannot close: " + openSubtasks + " subtask(s) are still open");
        }

        // Core workflow validation
        workflowService.validateTransition(issue.getProject().getId(),
            issue.getStatus(), transition.getToStatus());

        String oldStatus = issue.getStatus();
        issue.setStatus(transition.getToStatus());
        activityLog.log(issue, userId, "status", oldStatus, transition.getToStatus());

        return toDto(issueRepo.save(issue));
    }

    public void delete(Long id) {
        issueRepo.deleteById(id);
    }

    private IssueDto toDto(Issue i) {
        return new IssueDto(
            i.getId(), i.getProject().getId(), i.getType(), i.getPriority(), i.getStatus(),
            i.getTitle(), i.getDescription(),
            i.getAssignee() != null ? i.getAssignee().getId() : null,
            i.getAssignee() != null ? i.getAssignee().getName() : null,
            i.getReporter().getId(), i.getReporter().getName(),
            i.getParent() != null ? i.getParent().getId() : null,
            i.getSprint() != null ? i.getSprint().getId() : null,
            i.getStoryPoints(), i.getDueDate(), i.getVersion(),
            i.getCreatedAt(), i.getUpdatedAt());
    }
}
