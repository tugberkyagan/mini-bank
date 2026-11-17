package com.tugberk.minibank.account

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class AccountService(private val accountRepository: AccountRepository) {

    @Transactional(readOnly = true)
    fun getAccount(id: Long): AccountResponse =
        accountRepository.findByIdOrNull(id)?.toResponse()
            ?: throw AccountNotFoundException(id)

    @Transactional
    fun createAccount(request: CreateAccountRequest): AccountResponse =
        accountRepository.save(
            Account(request.ownerName, request.iban, BigDecimal.ZERO, request.currency)
        ).toResponse()
}

fun Account.toResponse() = AccountResponse(id!!, ownerName, iban, balance, currency)
