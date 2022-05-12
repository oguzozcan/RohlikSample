package com.sample.rohlik.synchronization

import com.sample.rohlik.compose.getFormattedTime
import com.sample.rohlik.data.ItemizationEntryDTO
import com.sample.rohlik.db.AmountDB
import com.sample.rohlik.db.ItemizationEntryDB
import java.util.*

fun mapDTOtoDBO(dto: ItemizationEntryDTO): ItemizationEntryDB {
    return ItemizationEntryDB(
        expenseId = dto.expenseId, location = dto.location?.name.orEmpty(),
        expenseType = dto.expenseType.name.orEmpty(),
        approvedAmount = AmountDB(
            dto.approvedAmount?.value ?: 0.0,
            dto.approvedAmount?.currencyCode ?: "USD"
        ),
        claimedAmount = AmountDB(
            dto.claimedAmount?.value ?: 0.0,
            dto.claimedAmount?.currencyCode ?: "USD"
        ),
        transactionAmount = AmountDB(
            dto.claimedAmount?.value ?: 0.0,
            dto.claimedAmount?.currencyCode ?: "USD"
        ),
        transactionDate = dto.transactionDate?.getFormattedTime() ?: "today",
        canDelete = dto.canDelete, state = DEFAULT.text

    )
}
