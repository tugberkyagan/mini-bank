package com.tugberk.minibank.deposit

class DepositNotFoundException(val id: Long) : RuntimeException("Deposit not found: $id")