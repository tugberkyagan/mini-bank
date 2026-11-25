package com.tugberk.minibank.deposit

import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class CreateDepositRequest(
    @field:Positive
    @field:Digits(integer = 17, fraction = 2)
    val amount: BigDecimal,

    @field:Pattern(regexp = "^[A-Z]{3}$")
    val currency: String,
)