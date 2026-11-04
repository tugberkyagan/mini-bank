package com.tugberk.minibank.account

import jakarta.validation.constraints.Positive
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.security.auth.login.AccountNotFoundException

@RestController
@RequestMapping("/accounts")
class AccountController {

    @GetMapping("/{id}")
    fun getAccount(@PathVariable @Positive id: Long): AccountResponse {
        if (id > 1000){
            throw AccountNotFoundException(id.toString())
        }
        return AccountResponse(
            id = id,
            owner = "Tuğberk Yağan",
            iban = "TR330006100519786457841326",
            balance = "1500.75".toBigDecimal(),
            currency = "TRY"
        )
    }
}