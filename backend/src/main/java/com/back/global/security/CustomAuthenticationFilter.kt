package com.back.global.security

import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.service.MemberService
import com.back.global.exception.ServiceException
import com.back.global.rq.Rq
import com.back.standard.util.Ut.json.toString
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class CustomAuthenticationFilter(
    private val memberService: MemberService,
    private val rq: Rq
) : OncePerRequestFilter() {

    companion object {
        private val PUBLIC_APIS = setOf(
            "/api/v1/members/login",
            "/api/v1/members/logout"
        )
        private val PUBLIC_API_PATTERNS = mapOf(
            Regex("/api/v\\d+/members") to setOf("POST") // 회원가입
        )
    }

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        logger.debug("Processing request for ${request.requestURI}")

        try {
            work(request, response, filterChain)
        } catch (e: ServiceException) {
            val rsData = e.rsData
            response.apply {
                contentType = "application/json;charset=UTF-8"
                status = rsData.statusCode
                writer.write(toString(rsData))
            }
        }
    }

    @Throws(ServletException::class, IOException::class)
    private fun work(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        // API 요청이 아니라면 패스
        if (!request.requestURI.startsWith("/api/")) {
            filterChain.doFilter(request, response)
            return
        }

        // 인증, 인가가 필요없는 API 요청이라면 패스
        if (PUBLIC_APIS.contains(request.requestURI) ||
            isPublicApiPattern(request.requestURI, request.method)) {
            filterChain.doFilter(request, response)
            return
        }

        // 이미 인증된 사용자가 있다면 (예: @WithUserDetails로 설정된 경우) 패스
        val existingAuth = SecurityContextHolder.getContext().authentication
        if (existingAuth != null && existingAuth.isAuthenticated && existingAuth.principal != "anonymousUser") {
            filterChain.doFilter(request, response)
            return
        }

        val (apiKey, accessToken) = extractCredentials()

        logger.debug("apiKey : $apiKey")
        logger.debug("accessToken : $accessToken")

        val isApiKeyExists = apiKey.isNotBlank()
        val isAccessTokenExists = accessToken.isNotBlank()

        if (!isApiKeyExists && !isAccessTokenExists) {
            filterChain.doFilter(request, response)
            return
        }


        var isAccessTokenValid = false

        val member: Member = if (isAccessTokenExists) {
            memberService.payload(accessToken)?.let { payload ->
                val id = payload["id"] as Int
                val username = payload["username"] as String
                val name = payload["name"] as String
                Member(id, username, name).also { isAccessTokenValid = true }
            } ?: memberService.findByApiKey(apiKey)
            ?: throw ServiceException("401-3", "API 키가 유효하지 않습니다.")
        } else {
            memberService.findByApiKey(apiKey)
                ?: throw ServiceException("401-3", "API 키가 유효하지 않습니다.")
        }


        if (isAccessTokenExists && !isAccessTokenValid) {
            val actorAccessToken = memberService.genAccessToken(member)
            rq.setCookie("accessToken", actorAccessToken)
            rq.setHeader("Authorization", actorAccessToken)
        }

        val user: UserDetails = SecurityUser(
            member.id,
            member.username,
            "",
            member.name,
            member.authorities
        )

        val authentication: Authentication = UsernamePasswordAuthenticationToken(
            user,
            user.password,
            user.authorities
        )

        // 이 시점 이후부터는 시큐리티가 이 요청을 인증된 사용자의 요청이다.
        SecurityContextHolder.getContext().authentication = authentication

        filterChain.doFilter(request, response)
    }

    private fun isPublicApiPattern(uri: String, method: String): Boolean {
        return PUBLIC_API_PATTERNS.any { (pattern, allowedMethods) ->
            pattern.matches(uri) && allowedMethods.contains(method)
        }
    }

    private fun extractCredentials(): Pair<String, String> {
        val headerAuthorization = rq.getHeader("Authorization", "")

        return if (headerAuthorization.isNotBlank()) {
            if (!headerAuthorization.startsWith("Bearer ")) {
                throw ServiceException("401-2", "Authorization 헤더가 Bearer 형식이 아닙니다.")
            }

            val headerAuthorizationBits = headerAuthorization.split(" ", limit = 3)
            val apiKey = headerAuthorizationBits[1]
            val accessToken = if (headerAuthorizationBits.size == 3) headerAuthorizationBits[2] else ""

            Pair(apiKey, accessToken)
        } else {
            val apiKey = rq.getCookieValue("apiKey", "")
            val accessToken = rq.getCookieValue("accessToken", "")
            Pair(apiKey, accessToken)
        }
    }
}
