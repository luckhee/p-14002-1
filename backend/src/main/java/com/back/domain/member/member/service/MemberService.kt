package com.back.domain.member.member.service

import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.repository.MemberRepository
import com.back.global.exception.ServiceException
import com.back.global.rsData.RsData
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class MemberService(
    private val authTokenService: AuthTokenService,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun count(): Long = memberRepository.count()

    fun join(
        username: String,
        password: String = "",
        nickname: String,
        profileImgUrl: String = ""
    ): Member {
        memberRepository.findByUsername(username).ifPresent {
            throw ServiceException("409-1", "이미 존재하는 아이디입니다.")
        }

        val encodedPassword = if (password.isNotBlank()) {
            passwordEncoder.encode(password)
        } else {
            ""
        }

        // Member의 primary constructor 순서에 맞게 수정
        val member = Member(
            username,
            encodedPassword,
            nickname,  // name 매개변수
            nickname,  // nickname 매개변수 (같은 값 사용)
            UUID.randomUUID().toString(), // apiKey
            profileImgUrl
        )
        return memberRepository.save(member)
    }

    fun findByUsername(username: String): Optional<Member> =
        memberRepository.findByUsername(username)

    fun findByApiKey(apiKey: String): Optional<Member> =
        memberRepository.findByApiKey(apiKey)

    fun genAccessToken(member: Member): String =
        authTokenService.genAccessToken(member)

    fun payload(accessToken: String): Map<String, Any>? =
        authTokenService.payload(accessToken)

    fun findById(id: Int): Optional<Member> =
        memberRepository.findById(id)

    fun findAll(): List<Member> =
        memberRepository.findAll()

    fun checkPassword(member: Member, password: String) {
        if (!passwordEncoder.matches(password, member.password)) {
            throw ServiceException("401-1", "비밀번호가 일치하지 않습니다.")
        }
    }

    fun modifyOrJoin(
        username: String,
        password: String = "",
        nickname: String,
        profileImgUrl: String = ""
    ): RsData<Member> {
        val existingMember = findByUsername(username).orElse(null)

        return if (existingMember == null) {
            val newMember = join(username, password, nickname, profileImgUrl)
            RsData("201-1", "회원가입이 완료되었습니다.", newMember)
        } else {
            modify(existingMember, nickname, profileImgUrl)
            RsData("200-1", "회원 정보가 수정되었습니다.", existingMember)
        }
    }

    private fun modify(member: Member, nickname: String, profileImgUrl: String) {
        member.modify(nickname, profileImgUrl)
    }
}
