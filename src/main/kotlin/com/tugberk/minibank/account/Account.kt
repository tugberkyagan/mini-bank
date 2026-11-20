package com.tugberk.minibank.account

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.hibernate.annotations.Generated
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(name = "accounts")
class Account (
    @Column(nullable = false)
    val ownerName: String = "",

    @Column(nullable = false, unique = true, length = 34)
    val iban: String = "",

    @Column(nullable = false, precision = 19, scale = 2)
    var balance: BigDecimal,

    @Column(nullable = false, length = 3)
    val currency: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Version
    val version: Long = 0

    @Generated
    @Column(insertable = false, updatable = false)
    val createdAt: OffsetDateTime? = null
}