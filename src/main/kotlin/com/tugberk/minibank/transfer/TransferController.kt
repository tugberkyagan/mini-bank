package com.tugberk.minibank.transfer

import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/transfers")
class TransferController(private val transferService: TransferService) {

    @GetMapping("/{id}")
    fun getTransfer(@PathVariable @Positive id: Long): TransferResponse {
        return transferService.getTransfer(id)
    }

    @PostMapping
    fun createTransfer(@Valid @RequestBody request: CreateTransferRequest): TransferResponse {
        return transferService.createTransfer(request)
    }
}