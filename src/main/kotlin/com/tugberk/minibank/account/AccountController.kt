package com.tugberk.minibank.account

import com.tugberk.minibank.deposit.CreateDepositRequest
import com.tugberk.minibank.deposit.DepositResponse
import com.tugberk.minibank.deposit.DepositService
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/accounts")
class AccountController(
    private val accountService: AccountService,
    private val depositService: DepositService,
) {

    @GetMapping("/{id}")
    fun getAccount(@PathVariable @Positive id: Long): AccountResponse {
        return accountService.getAccount(id)
    }

    @PostMapping
    fun createAccount(@Valid @RequestBody request: CreateAccountRequest): AccountResponse {
        return accountService.createAccount(request)
    }

    @GetMapping("/deposits/{depositId}")
    fun getDeposit(
        @PathVariable @Positive depositId: Long
    ): DepositResponse {
        return depositService.getDeposit(depositId)
    }

    @PostMapping("/{accountId}/deposits")
    @ResponseStatus(HttpStatus.CREATED)
    fun createDeposit(
        @PathVariable @Positive accountId: Long,
        @RequestHeader("Idempotency-Key") idempotencyKey: UUID,
        @Valid @RequestBody request: CreateDepositRequest,
    ): DepositResponse = depositService.createDeposit(idempotencyKey, accountId, request)
}