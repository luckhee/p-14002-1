package com.back.domain.member.member.entity

import com.back.global.jpa.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.*

@Entity
class Member(
    @Column(unique = true)
    var username: String,

    var password: String,

    var name: String,

    var nickname : String,

    @Column(unique = true)
    var apiKey: String = UUID.randomUUID().toString(),

    var profileImgUrl: String = DEFAULT_PROFILE_IMG_URL
) : BaseEntity() {

    companion object {
        private const val DEFAULT_PROFILE_IMG_URL = "https://placehold.co/600x600?text=U_U"
        private val ADMIN_USERNAMES = setOf("system", "admin")
    }

    // JPA를 위한 기본 생성자
    constructor() : this("","", "", "", UUID.randomUUID().toString(), DEFAULT_PROFILE_IMG_URL)

    // 기존 생성자들을 위한 팩토리 메서드들
    constructor(id: Int, username: String, nickname: String) : this() {
        this.username = username
        this.name = nickname
        // id는 BaseEntity에서 관리되므로 별도 설정 불필요
    }


    fun modifyApiKey(newApiKey: String) {
        this.apiKey = newApiKey
    }

    val isAdmin: Boolean
        get() = username in ADMIN_USERNAMES

    val authorities: Collection<GrantedAuthority>
        get() = authoritiesAsStringList.map { SimpleGrantedAuthority(it) }

    private val authoritiesAsStringList: List<String>
        get() = buildList {
            if (isAdmin) add("ROLE_ADMIN")
        }

    fun modify(nickname: String, newProfileImgUrl: String = DEFAULT_PROFILE_IMG_URL) {
        this.name = nickname
        this.profileImgUrl = newProfileImgUrl
    }

    val profileImgUrlOrDefault: String
        get() = profileImgUrl.ifBlank { DEFAULT_PROFILE_IMG_URL }
}
