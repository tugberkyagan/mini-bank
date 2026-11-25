package com.tugberk.minibank.deposit

import java.math.BigDecimal
import java.time.OffsetDateTime

data class DepositResponse(
    val id: Long,
    val toAccountId: Long,
    val amount: BigDecimal,
    val currency: String,
    val createdAt: OffsetDateTime?,
)

fun Deposit.toResponse() = DepositResponse(
    id = id!!,
    toAccountId = toAccountId,
    amount = amount,
    currency = currency,
    createdAt = createdAt,
)