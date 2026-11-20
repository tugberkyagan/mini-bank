package com.tugberk.minibank.transfer

class InsufficientFundsException(accountId: Long) :
    RuntimeException("Insufficient funds in account $accountId")