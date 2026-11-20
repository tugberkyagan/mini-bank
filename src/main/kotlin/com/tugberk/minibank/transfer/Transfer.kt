package com.tugberk.minibank.transfer

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Generated
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "transfers")
class Transfer(
    @Column(nullable = false, unique = true, updatable = false)
    val idempotencyKey: UUID,

    @Column(nullable = false, updatable = false)
    val fromAccountId: Long,

    @Column(nullable = false, updatable = false)
    val toAccountId: Long,

    @Column(nullable = false, updatable = false, precision = 19, scale = 2)
    val amount: BigDecimal,

    @Column(nullable = false, updatable = false, length = 3)
    val currency: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Generated
    @Column(insertable = false, updatable = false)
    val createdAt: OffsetDateTime? = null
}