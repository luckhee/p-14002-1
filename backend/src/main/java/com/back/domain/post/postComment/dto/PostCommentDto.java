package com.back.domain.post.postComment.dto;

import com.back.domain.post.postComment.entity.PostComment;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

public record PostCommentDto(
        @NonNull int id,
        @NonNull LocalDateTime createDate,
        @NonNull LocalDateTime modifyDate,
        @NonNull int authorId,
        @NonNull String authorName,
        @NonNull int postId,
        @NonNull String content
) {
    public PostCommentDto(PostComment postComment) {
        this(
                postComment.getId(),
                postComment.getCreateDate(),
                postComment.getModifyDate(),
                postComment.getAuthor().getId(),
                postComment.getAuthor().getName(),
                postComment.getPost().getId(),
                postComment.getContent()
        );
    }
}