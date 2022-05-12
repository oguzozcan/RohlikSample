package com.sample.rohlik.data

import java.util.*

data class ItemizationEntryDTO(
    val expenseId: String,
    val expenseType: ExpenseTypeDTO,
    val canDelete: Boolean,
    val transactionDate: Date?,
    val location: LocationDTO?,
    val transactionAmount: AmountDTO,
    val approvedAmount: AmountDTO?,
    val claimedAmount: AmountDTO?
)

data class AmountDTO(
    val value: Double,
    val currencyCode: String
)

data class ExpenseTypeDTO(
    val name: String?
)

data class LocationDTO(
    val name: String
)
