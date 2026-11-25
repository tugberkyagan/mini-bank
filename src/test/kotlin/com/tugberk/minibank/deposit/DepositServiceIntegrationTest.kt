package com.tugberk.minibank.deposit

import com.tugberk.minibank.TestcontainersConfiguration
import com.tugberk.minibank.account.Account
import com.tugberk.minibank.account.AccountRepository
import com.tugberk.minibank.transfer.LedgerEntryRepository
import com.tugberk.minibank.transfer.TransferRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import java.math.BigDecimal
import java.util.UUID

@SpringBootTest
@Import(TestcontainersConfiguration::class)
class DepositServiceIntegrationTest @Autowired constructor(
    private val accountRepository: AccountRepository,
    private val ledgerEntryRepository: LedgerEntryRepository,
    private val depositRepository: DepositRepository,
    private val transferRepository: TransferRepository,
    private val depositService: DepositService,
) {
    @BeforeEach
    fun cleanUp() {
        ledgerEntryRepository.deleteAll()
        transferRepository.deleteAll()
        depositRepository.deleteAll()
        accountRepository.deleteAll()
    }

    private fun account(iban: String, balance: String): Long =
        accountRepository.save(Account("Test", iban, BigDecimal(balance), "TRY")).id!!

    private fun request(amount: String, currency: String = "TRY") =
        CreateDepositRequest(BigDecimal(amount), currency)

    private fun balanceOf(id: Long) = accountRepository.findById(id).get().balance

    @Test
    fun `deposit money and writes a ledger entry`() {
        val to = account("TR000000000000000000000002", "0.00")

        depositService.createDeposit(UUID.randomUUID(),to, request("30.00"))

        assertThat(balanceOf(to)).isEqualByComparingTo("30.00")
        val entries = ledgerEntryRepository.findAll()
        assertThat(entries).hasSize(1)
        assertThat(entries[0].depositId).isNotNull
        assertThat(entries.sumOf { it.amount }).isEqualByComparingTo("30.00")
    }

    @Test
    fun `same idempotency key does not charge twice`() {
        val to = account("TR000000000000000000000001", "0.00")
        val key = UUID.randomUUID()

        val first = depositService.createDeposit(key, to, request("30.00"))
        val second = depositService.createDeposit(key, to, request("30.00"))

        assertThat(second.id).isEqualTo(first.id)
        assertThat(balanceOf(to)).isEqualByComparingTo("30.00")
        assertThat(depositRepository.count()).isEqualTo(1)
    }

    @Test
    fun `throw currency validation error and validate rollback system`() {
        val to = account("TR000000000000000000000001", "0.00")

        assertThrows<InvalidDepositException> {
            depositService.createDeposit(
                idempotencyKey = UUID.randomUUID(),
                accountId = to,
                request(amount = "30.00", currency = "USD")
            )
        }

        assertThat(balanceOf(to)).isEqualByComparingTo("0.00")
        val entries = ledgerEntryRepository.findAll()
        assertThat(entries).hasSize(0)
    }
}