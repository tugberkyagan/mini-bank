package com.tugberk.minibank.account

import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/accounts")
class AccountController(private val accountService: AccountService) {

    @GetMapping("/{id}")
    fun getAccount(@PathVariable @Positive id: Long): AccountResponse {
        return accountService.getAccount(id)
    }

    @PostMapping
    fun createAccount(@Valid @RequestBody request: CreateAccountRequest): AccountResponse {
        return accountService.createAccount(request)
    }
}