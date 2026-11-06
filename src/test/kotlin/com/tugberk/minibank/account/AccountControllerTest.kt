package com.tugberk.minibank.account

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import kotlin.test.Test

@WebMvcTest(AccountController::class)
class AccountControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `returns account when id is valid`() {
        mockMvc.get("/accounts/42")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(42) }
                jsonPath("$.currency") { value("TRY") }
            }
    }

    @Test
    fun `returns 404 when account does not exist`() {
        mockMvc.get("/accounts/5000")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.code") { value("ACCOUNT_NOT_FOUND") }
                jsonPath("$.message") { value("No account with id 5000 exists.") }
            }
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