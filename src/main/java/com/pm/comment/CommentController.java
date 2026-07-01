package com.pm.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.pm.common.AuthHelper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    private Long getUserId() { return AuthHelper.getUserId(); }

    @PostMapping("/issues/{issueId}/comments")
    public ResponseEntity<CommentDto> create(@PathVariable Long issueId,
                                             @RequestBody CreateCommentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(commentService.create(issueId, req.body(), getUserId()));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
