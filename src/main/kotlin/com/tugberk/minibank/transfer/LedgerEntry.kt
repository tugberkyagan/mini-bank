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

@Entity
@Table(name = "ledger_entries")
class LedgerEntry(
    @Column(nullable = false, updatable = false)
    val accountId: Long,

    @Column(nullable = false, updatable = false, precision = 19, scale = 2)
    val amount: BigDecimal,

    @Column(updatable = false)
    val transferId: Long? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Generated
    @Column(insertable = false, updatable = false)
    val createdAt: OffsetDateTime? = null
}