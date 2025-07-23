package com.back.domain.member.member.dto;

import com.back.domain.member.member.entity.Member;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

public record MemberWithUsernameDto(
        @NonNull int id,
        @NonNull LocalDateTime createDate,
        @NonNull LocalDateTime modifyDate,
        @NonNull String name,
        @NonNull String username,
        @NonNull boolean isAdmin
) {
    public MemberWithUsernameDto(int id, LocalDateTime createDate, LocalDateTime modifyDate, String name, String username, boolean isAdmin) {
        this.id = id;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
        this.name = name;
        this.username = username;
        this.isAdmin = isAdmin;
    }

    public MemberWithUsernameDto(Member member) {
        this(
                member.getId(),
                member.getCreateDate(),
                member.getModifyDate(),
                member.getName(),
                member.getUsername(),
                member.isAdmin()
        );
    }
}
