package com.tugberk.minibank.transfer

class TransferNotFoundException(val id: Long) : RuntimeException("Transfer not found: $id")