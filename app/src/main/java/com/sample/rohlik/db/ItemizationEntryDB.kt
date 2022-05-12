package com.sample.rohlik.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ItemizationEntryDB(
    @PrimaryKey val expenseId: String,
    @ColumnInfo(name = "expense_type") val expenseType: String,
    @ColumnInfo(name = "transaction_date") val transactionDate: String,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "can_delete") val canDelete: Boolean,
    @ColumnInfo(name = "transaction_amount") val transactionAmount: AmountDB,
    @ColumnInfo(name = "approved_amount") val approvedAmount: AmountDB,
    @ColumnInfo(name = "claimed_amount") val claimedAmount: AmountDB,
    @ColumnInfo(name = "state") val state: String
)

@Entity
data class AmountDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo val amount: Double,
    @ColumnInfo val currencyCode: String
)