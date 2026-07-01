package com.pm.comment;

import java.time.LocalDateTime;

public record CommentDto(Long id, Long issueId, Long userId, String userName,
                         String body, LocalDateTime createdAt) {}
