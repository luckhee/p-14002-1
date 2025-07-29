package com.back.domain.member.member.dto;

import com.back.domain.member.member.entity.Member;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

public record MemberDto(
        @NonNull int id,
        @NonNull LocalDateTime createDate,
        @NonNull LocalDateTime modifyDate,
        @NonNull String name,
        @NonNull boolean isAdmin,
        @NonNull String profileImageUrl
) {
    public MemberDto(int id, LocalDateTime createDate, LocalDateTime modifyDate, String name, boolean isAdmin, String profileImageUrl) {
        this.id = id;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
        this.name = name;
        this.isAdmin = isAdmin;
        this.profileImageUrl = profileImageUrl;
    }

    public MemberDto(Member member) {
        this(
                member.getId(),
                member.getCreateDate(),
                member.getModifyDate(),
                member.getName(),
                member.isAdmin(),
                member.getProfileImgUrlOrDefault()
        );
    }
}
