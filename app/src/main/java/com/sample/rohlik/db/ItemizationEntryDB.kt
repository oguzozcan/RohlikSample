package com.sample.rohlik.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ItemizationEntryDB(
    @PrimaryKey val expenseId: String,
    @ColumnInfo(name = "expense_type") val expenseType: String,
    @ColumnInfo(name = "transaction_date") val transactionDate: String,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "can_delete") val canDelete: Boolean,
    @Embedded(prefix = "transaction_amount") val transactionAmount: AmountDB,
    @Embedded(prefix = "approved_amount") val approvedAmount: AmountDB,
    @Embedded(prefix = "claimed_amount") val claimedAmount: AmountDB,
    @ColumnInfo(name = "state") val state: String
)

@Entity
data class AmountDB(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo val amount: Double,
    @ColumnInfo val currencyCode: String) {
    constructor(amount: Double, currencyCode: String) : this(0, amount, currencyCode)
}