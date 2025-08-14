package com.back.global.jpa.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass // 엔티티의 부모 클래스에는 이걸 달아야 한다.
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Int = 0

    @CreatedDate
    @Column(nullable = false, updatable = false)
    open val createDate: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @Column(nullable = false)
    open val modifyDate: LocalDateTime = LocalDateTime.now()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as BaseEntity
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
