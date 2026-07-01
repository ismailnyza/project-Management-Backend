package com.pm.issue;

import com.pm.issue.dto.CreateIssueRequest;
import com.pm.issue.dto.IssueDto;
import com.pm.issue.enums.IssueType;
import com.pm.project.Project;
import com.pm.project.ProjectRepository;
import com.pm.user.User;
import com.pm.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssueServiceTest {
    @Mock private IssueRepository issueRepo;
    @Mock private ProjectRepository projectRepo;
    @Mock private UserRepository userRepo;
    @Mock private com.pm.workflow.WorkflowService workflowService;
    @Mock private com.pm.workflow.WorkflowTransitionRepository transitionRepo;
    @Mock private com.pm.activity.ActivityLogService activityLog;
    @Mock private com.pm.sprint.SprintRepository sprintRepo;
    @InjectMocks private IssueService issueService;

    @Test
    void createShouldFailForNonExistentProject() {
        when(projectRepo.findById(999L)).thenReturn(Optional.empty());
        var req = new CreateIssueRequest(IssueType.TASK, "Title", null, null, null, null, null, null, null, null);
        assertThrows(IllegalArgumentException.class, () -> issueService.create(999L, req, 1L));
    }

    @Test
    void createShouldSetSubtaskTypeWhenParentIsNotEpic() {
        Project project = new Project(); project.setId(1L);
        User user = new User(); user.setId(1L);
        Issue parent = new Issue(); parent.setId(10L); parent.setProject(project); parent.setType(IssueType.STORY);

        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
        when(userRepo.getReferenceById(1L)).thenReturn(user);
        when(issueRepo.findById(10L)).thenReturn(Optional.of(parent));
        when(issueRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        var req = new CreateIssueRequest(IssueType.TASK, "Title", null, null, null, null, 10L, null, null, null);
        IssueDto result = issueService.create(1L, req, 1L);

        assertEquals(IssueType.SUBTASK, result.type());
    }

    @Test
    void createShouldSetStoryTypeWhenParentIsEpic() {
        Project project = new Project(); project.setId(1L);
        User user = new User(); user.setId(1L);
        Issue parent = new Issue(); parent.setId(10L); parent.setProject(project); parent.setType(IssueType.EPIC);

        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
        when(userRepo.getReferenceById(1L)).thenReturn(user);
        when(issueRepo.findById(10L)).thenReturn(Optional.of(parent));
        when(issueRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        var req = new CreateIssueRequest(IssueType.TASK, "Title", null, null, null, null, 10L, null, null, null);
        IssueDto result = issueService.create(1L, req, 1L);

        assertEquals(IssueType.STORY, result.type());
    }
}
