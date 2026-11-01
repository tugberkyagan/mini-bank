package com.tugberk.minibank.account

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/accounts")
class AccountController {

    @GetMapping("/{id}")
    fun getAccount(@PathVariable id: Long): AccountResponse {
        return AccountResponse(
            id = id,
            owner = "Tuğberk Yağan",
            iban = "TR330006100519786457841326",
            balance = "1500.75".toBigDecimal(),
            currency = "TRY"
        )
    }
}