package com.back.domain.post.post.dto

import com.back.domain.post.post.entity.Post
import lombok.Getter
import java.time.LocalDateTime

@Getter
data class PostWithContentDto(
    val id: Int,
    val createDate: LocalDateTime,
    val modifyDate: LocalDateTime,
    val authorId: Int,
    val authorName: String,
    val title: String,
    val content: String
) {
    constructor(post: Post) : this(
        post.id,
        post.createDate,
        post.modifyDate,
        post.author.id,
        post.author.name,
        post.title,
        post.content
    )
}
