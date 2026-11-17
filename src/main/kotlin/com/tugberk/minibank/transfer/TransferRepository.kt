package com.tugberk.minibank.transfer

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TransferRepository : JpaRepository<Transfer, Long> {
    fun findByIdempotencyKey(idempotencyKey: UUID): Transfer?
}