package com.tugberk.minibank.transfer

import com.tugberk.minibank.TestcontainersConfiguration
import com.tugberk.minibank.account.Account
import com.tugberk.minibank.account.AccountRepository
import com.tugberk.minibank.deposit.DepositRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import java.math.BigDecimal
import java.util.UUID
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
@Import(TestcontainersConfiguration::class)
class TransferServiceIntegrationTest @Autowired constructor(
    private val transferService: TransferService,
    private val accountRepository: AccountRepository,
    private val transferRepository: TransferRepository,
    private val ledgerEntryRepository: LedgerEntryRepository,
    private val depositRepository: DepositRepository,
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

    private fun request(from: Long, to: Long, amount: String) =
        CreateTransferRequest(from, to, BigDecimal(amount), "TRY")

    private fun balanceOf(id: Long) = accountRepository.findById(id).get().balance

    @Test
    fun `moves money and writes two ledger entries`() {
        val from = account("TR000000000000000000000001", "100.00")
        val to = account("TR000000000000000000000002", "0.00")

        transferService.createTransfer(UUID.randomUUID(), request(from, to, "30.00"))

        assertThat(balanceOf(from)).isEqualByComparingTo("70.00")
        assertThat(balanceOf(to)).isEqualByComparingTo("30.00")
        val entries = ledgerEntryRepository.findAll()
        assertThat(entries).hasSize(2)
        assertThat(entries.sumOf { it.amount }).isEqualByComparingTo("0")
    }

    @Test
    fun `same idempotency key does not charge twice`() {
        val from = account("TR000000000000000000000001", "100.00")
        val to = account("TR000000000000000000000002", "0.00")
        val key = UUID.randomUUID()

        val first = transferService.createTransfer(key, request(from, to, "30.00"))
        val second = transferService.createTransfer(key, request(from, to, "30.00"))

        assertThat(second.id).isEqualTo(first.id)
        assertThat(balanceOf(from)).isEqualByComparingTo("70.00")
        assertThat(transferRepository.count()).isEqualTo(1)
    }

    @Test
    fun `insufficient funds rolls back everything`() {
        val from = account("TR000000000000000000000001", "100.00")
        val to = account("TR000000000000000000000002", "0.00")

        assertThrows<InsufficientFundsException> {
            transferService.createTransfer(UUID.randomUUID(), request(from, to, "1000.00"))
        }

        assertThat(balanceOf(from)).isEqualByComparingTo("100.00")
        assertThat(transferRepository.count()).isZero()
        assertThat(ledgerEntryRepository.count()).isZero()
    }

    @Test
    fun `concurrent transfers never overdraw the account`() {
        val from = account("TR000000000000000000000001", "100.00")
        val to = account("TR000000000000000000000002", "0.00")
        val threadCount = 20
        val executor = Executors.newFixedThreadPool(threadCount)
        val start = CountDownLatch(1)

        val futures = (1..threadCount).map {
            executor.submit(Callable {
                start.await()
                try {
                    transferService.createTransfer(UUID.randomUUID(), request(from, to, "10.00"))
                    true
                } catch (e: InsufficientFundsException) {
                    false
                }
            })
        }
        start.countDown()
        val successCount = futures.count { it.get() }
        executor.shutdown()

        assertThat(successCount).isEqualTo(10)
        assertThat(balanceOf(from)).isEqualByComparingTo("0.00")
        assertThat(balanceOf(to)).isEqualByComparingTo("100.00")
        assertThat(ledgerEntryRepository.count()).isEqualTo(20)
    }
}