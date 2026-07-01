package com.pm.activity;

import com.pm.issue.Issue;
import com.pm.user.User;
import com.pm.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityLogService {
    private final ActivityLogRepository activityLogRepo;
    private final UserRepository userRepo;

    @Transactional(propagation = Propagation.MANDATORY)
    public void log(Issue issue, Long userId, String fieldName, String oldValue, String newValue) {
        ActivityLog log = new ActivityLog();
        log.setIssue(issue);
        log.setUser(userRepo.getReferenceById(userId));
        log.setAction("updated");
        log.setFieldName(fieldName);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        activityLogRepo.save(log);
    }
}
