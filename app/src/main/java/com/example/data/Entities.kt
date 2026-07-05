package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val initialBalance: Double,
    val colorHex: String
)

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val type: String, // "INCOME", "EXPENSE", "TRANSFER"
    val category: String, // e.g., "Food", "Salary", "Transfer"
    val accountId: Int, // Source account (or only account for Income/Expense)
    val transferToAccountId: Int? = null, // Destination account if type is "TRANSFER"
    val timestamp: Long,
    val description: String
)
