package com.tugberk.minibank.account

import java.math.BigDecimal

data class AccountResponse(
    val id: Long,
    val owner: String,
    val iban: String,
    val balance: BigDecimal,
    val currency: String,
)
