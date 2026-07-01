package com.pm.issue;

import com.pm.issue.enums.IssueType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    @Query("""
        SELECT i FROM Issue i
        WHERE i.project.id = :projectId AND i.parent IS NULL
        AND (:type IS NULL OR i.type = :type)
        AND (:status IS NULL OR i.status = :status)
        AND (:assigneeId IS NULL OR i.assignee.id = :assigneeId)
        AND (:search IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :search, '%'))
             OR LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%')))
        ORDER BY i.id DESC
        """)
    Page<Issue> findBoardIssues(@Param("projectId") Long projectId,
                                @Param("type") IssueType type,
                                @Param("status") String status,
                                @Param("assigneeId") Long assigneeId,
                                @Param("search") String search,
                                Pageable pageable);

    List<Issue> findByParentIdOrderByIdAsc(Long parentId);

    @Query("SELECT i FROM Issue i WHERE i.parent.id = :parentId ORDER BY i.id")
    List<Issue> findSubtasks(@Param("parentId") Long parentId);

    @Query("SELECT COUNT(i) FROM Issue i WHERE i.project.id = :projectId AND i.parent IS NULL")
    long countByProjectId(@Param("projectId") Long projectId);

    List<Issue> findBySprintIdOrderByIdAsc(Long sprintId);

    @Query("SELECT i FROM Issue i WHERE i.project.id = :projectId AND i.parent.id = :epicId")
    List<Issue> findStoriesByEpic(@Param("projectId") Long projectId, @Param("epicId") Long epicId);
}
