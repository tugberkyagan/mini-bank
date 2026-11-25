package com.tugberk.minibank.common

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import com.tugberk.minibank.account.AccountNotFoundException
import com.tugberk.minibank.deposit.DepositNotFoundException
import com.tugberk.minibank.deposit.InvalidDepositException
import com.tugberk.minibank.transfer.InsufficientFundsException
import com.tugberk.minibank.transfer.InvalidTransferException
import com.tugberk.minibank.transfer.TransferNotFoundException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.method.annotation.HandlerMethodValidationException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(AccountNotFoundException::class)
    fun handleAccountNotFound(e: AccountNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse("ACCOUNT_NOT_FOUND", "No account with id ${e.id} exists."))

    @ExceptionHandler(TransferNotFoundException::class)
    fun handleTransferNotFound(e: TransferNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse("TRANSFER_NOT_FOUND", "No transfer with id ${e.id} exists."))

    @ExceptionHandler(DepositNotFoundException::class)
    fun handleDepositNotFound(e: DepositNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse("DEPOSIT_NOT_FOUND", "No deposit with id ${e.id} exists."))

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(e: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> =
        ResponseEntity.badRequest()
            .body(ErrorResponse("INVALID_PARAMETER", "Invalid value for '${e.name}'"))

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        log.error("Unexpected error", e)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse("INTERNAL_ERROR", "Something went wrong"))

    }
    @ExceptionHandler(HandlerMethodValidationException::class)
    fun handleValidation(e: HandlerMethodValidationException): ResponseEntity<ErrorResponse> =
        ResponseEntity.badRequest()
            .body(ErrorResponse("INVALID_PARAMETER", "Value must be positive"))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleInvalidRequestBody(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> =
        ResponseEntity.badRequest()
            .body(ErrorResponse("VALIDATION_FAILED",
                e.bindingResult.fieldErrors.joinToString { "${it.field}: ${it.defaultMessage}" }))

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolation(e: DataIntegrityViolationException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse("CONFLICT", "Request conflicts with existing data"))

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadableBody(ex: HttpMessageNotReadableException) =
        ResponseEntity.badRequest().body(ErrorResponse("MALFORMED_REQUEST", "Request body is missing or malformed"))

    @ExceptionHandler(InsufficientFundsException::class)
    fun handleInsufficientFunds(ex: InsufficientFundsException) =
        ResponseEntity.unprocessableContent()
            .body(ErrorResponse("INSUFFICIENT_FUNDS", ex.message ?: "Insufficient funds"))

    @ExceptionHandler(InvalidTransferException::class)
    fun handleInvalidTransfer(ex: InvalidTransferException) =
        ResponseEntity.unprocessableContent()
            .body(ErrorResponse("INVALID_TRANSFER", ex.message ?: "Invalid transfer"))

    @ExceptionHandler(InvalidDepositException::class)
    fun handleInvalidDeposit(ex: InvalidDepositException) =
        ResponseEntity.unprocessableContent()
            .body(ErrorResponse("INVALID_DEPOSIT", ex.message ?: "Invalid deposit"))

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun handleMissingHeader(ex: MissingRequestHeaderException) =
        ResponseEntity.badRequest()
            .body(ErrorResponse("MISSING_HEADER", "Required header '${ex.headerName}' is missing"))
}