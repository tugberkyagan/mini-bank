package com.tugberk.minibank.transfer

import com.tugberk.minibank.account.AccountNotFoundException
import com.tugberk.minibank.account.AccountRepository
import com.tugberk.minibank.account.AccountService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Service
class TransferService(
    private val transferRepository: TransferRepository,
    private val accountRepository: AccountRepository,
    private val ledgerEntryRepository: LedgerEntryRepository,
) {
    @Transactional
    fun createTransfer(idempotencyKey: UUID, request: CreateTransferRequest): TransferResponse {
        transferRepository.findByIdempotencyKey(idempotencyKey)?.let { return it.toResponse() }

        if (request.fromAccountId == request.toAccountId) {
            throw InvalidTransferException("Cannot transfer to the same account")
        }

        val (firstId, secondId) = listOf(request.fromAccountId, request.toAccountId).sorted()

        val first = accountRepository.findByIdForUpdate(firstId) ?: throw AccountNotFoundException(firstId)
        val second = accountRepository.findByIdForUpdate(secondId) ?: throw AccountNotFoundException(secondId)

        val from = if (first.id == request.fromAccountId) first else second
        val to = if (second.id == request.toAccountId) second else first

        if (from.currency != request.currency || to.currency != request.currency) {
            throw InvalidTransferException("Currency mismatch")
        }

        if (from.balance < request.amount) {
            throw InsufficientFundsException(request.fromAccountId)
        }

        from.balance -= request.amount
        to.balance += request.amount

        val transfer = transferRepository.save(
            Transfer(idempotencyKey, request.fromAccountId, request.toAccountId, request.amount, request.currency)
        )

        ledgerEntryRepository.saveAll(listOf(
            LedgerEntry(accountId = request.fromAccountId, amount = request.amount.negate(), transferId = transfer.id),
            LedgerEntry(accountId = request.toAccountId, amount = request.amount, transferId = transfer.id)
        ))

        return transfer.toResponse()
    }

    @Transactional(readOnly = true)
    fun getTransfer(id: Long): TransferResponse =
        transferRepository.findByIdOrNull(id)?.toResponse() ?: throw TransferNotFoundException(id)
}
