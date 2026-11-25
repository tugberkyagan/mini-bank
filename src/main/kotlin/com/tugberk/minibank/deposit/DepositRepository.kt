package com.tugberk.minibank.deposit

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DepositRepository : JpaRepository<Deposit, Long> {
    fun findByIdempotencyKey(idempotencyKey: UUID): Deposit?
}