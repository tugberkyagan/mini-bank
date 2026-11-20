package com.tugberk.minibank.transfer

import java.math.BigDecimal
import java.time.OffsetDateTime

data class TransferResponse(
    val id: Long,
    val fromAccountId: Long,
    val toAccountId: Long,
    val amount: BigDecimal,
    val currency: String,
    val createdAt: OffsetDateTime?,
)

fun Transfer.toResponse() = TransferResponse(
    id = id!!,
    fromAccountId = fromAccountId,
    toAccountId = toAccountId,
    amount = amount,
    currency = currency,
    createdAt = createdAt,
)