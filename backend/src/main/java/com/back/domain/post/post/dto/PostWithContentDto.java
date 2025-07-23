package com.back.domain.post.post.dto;

import com.back.domain.post.post.entity.Post;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

public record PostWithContentDto(
        @NonNull int id,
        @NonNull LocalDateTime createDate,
        @NonNull LocalDateTime modifyDate,
        @NonNull int authorId,
        @NonNull String authorName,
        @NonNull String title,
        @NonNull String content
) {
    public PostWithContentDto(Post post) {
        this(
                post.getId(),
                post.getCreateDate(),
                post.getModifyDate(),
                post.getAuthor().getId(),
                post.getAuthor().getName(),
                post.getTitle(),
                post.getContent()
        );
    }
}