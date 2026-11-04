package com.tugberk.minibank.account

class AccountNotFoundException(val id: Long) : RuntimeException("Account not found: $id")