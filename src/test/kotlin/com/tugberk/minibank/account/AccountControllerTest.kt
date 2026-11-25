package com.tugberk.minibank.account

import com.tugberk.minibank.deposit.DepositService
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import kotlin.test.Test

@WebMvcTest(AccountController::class)
class AccountControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var accountService: AccountService

    @MockitoBean
    lateinit var depositService: DepositService

    @Test
    fun `returns account when id is valid`() {
        given(accountService.getAccount(42))
            .willReturn(AccountResponse(42, "Tuğberk", "TR330006100519786457841326", "100.00".toBigDecimal(), "TRY"))
        mockMvc.get("/accounts/42").andExpect { /* eskisi gibi */ }
    }

    @Test
    fun `returns 404 when account does not exist`() {
        given(accountService.getAccount(5000)).willThrow(AccountNotFoundException(5000))
    }

    @Test
    fun `returns 400 when id is not a number`() {
        mockMvc.get("/accounts/abc")
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.code") { value("INVALID_PARAMETER") }
                jsonPath("$.message") { value("Invalid value for 'id'") }
            }
    }

    @Test
    fun `returns 400 when id is not positive`() {
        mockMvc.get("/accounts/-5")
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.code") { value("INVALID_PARAMETER") }
                jsonPath("$.message") { value("Value must be positive") }
            }
    }
}