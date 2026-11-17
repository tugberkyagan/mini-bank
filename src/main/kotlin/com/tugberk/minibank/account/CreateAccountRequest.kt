package com.tugberk.minibank.account

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class CreateAccountRequest(
    @field:NotBlank val ownerName: String,
    @field:Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z0-9]{11,30}$") val iban: String,
    @field:Pattern(regexp = "^[A-Z]{3}$") val currency: String,
)