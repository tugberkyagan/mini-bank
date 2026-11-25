package com.tugberk.minibank.deposit

import com.tugberk.minibank.account.AccountNotFoundException
import com.tugberk.minibank.account.AccountRepository
import com.tugberk.minibank.transfer.InvalidTransferException
import com.tugberk.minibank.transfer.LedgerEntry
import com.tugberk.minibank.transfer.LedgerEntryRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class DepositService(
    private val depositRepository: DepositRepository,
    private val accountRepository: AccountRepository,
    private val ledgerEntryRepository: LedgerEntryRepository,
) {
    @Transactional
    fun createDeposit(idempotencyKey: UUID, accountId: Long, request: CreateDepositRequest): DepositResponse {
        depositRepository.findByIdempotencyKey(idempotencyKey)?.let { return it.toResponse() }

        val to = accountRepository.findByIdForUpdate(accountId) ?: throw AccountNotFoundException(accountId)

        if(to.currency != request.currency) {
            throw InvalidDepositException("Currency unmatched for ${accountId}")
        }

        to.balance += request.amount

        val deposit = depositRepository.save(
            Deposit(idempotencyKey, accountId, request.amount, request.currency)
        )

        ledgerEntryRepository.saveAll(listOf(
            LedgerEntry(accountId = accountId, amount = request.amount, depositId = deposit.id)
        ))

        return deposit.toResponse()
    }

    @Transactional(readOnly = true)
    fun getDeposit(id: Long): DepositResponse =
        depositRepository.findByIdOrNull(id)?.toResponse() ?: throw DepositNotFoundException(id)
}
