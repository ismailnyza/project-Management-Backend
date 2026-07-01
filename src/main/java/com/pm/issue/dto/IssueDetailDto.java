package com.pm.issue.dto;

import com.pm.comment.CommentDto;
import java.util.List;

public record IssueDetailDto(IssueDto issue, List<IssueDto> subtasks, List<CommentDto> comments) {}
