package com.pm.comment;

import com.pm.issue.Issue;
import com.pm.issue.IssueRepository;
import com.pm.user.User;
import com.pm.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepo;
    private final IssueRepository issueRepo;
    private final UserRepository userRepo;

    public CommentDto create(Long issueId, String body, Long userId) {
        Issue issue = issueRepo.findById(issueId)
            .orElseThrow(() -> new IllegalArgumentException("Issue not found"));
        User user = userRepo.getReferenceById(userId);
        Comment c = new Comment();
        c.setIssue(issue);
        c.setUser(user);
        c.setBody(body);
        c = commentRepo.save(c);
        return new CommentDto(c.getId(), c.getIssue().getId(), c.getUser().getId(),
            c.getUser().getName(), c.getBody(), c.getCreatedAt());
    }

    public void delete(Long commentId) {
        commentRepo.deleteById(commentId);
    }
}
