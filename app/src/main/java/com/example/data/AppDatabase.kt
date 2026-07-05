package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Account::class, Transaction::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "financial_tracker_database"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.accountDao())
                }
            }
        }

        suspend fun populateDatabase(accountDao: AccountDao) {
            // Check if any account exists
            if (accountDao.getAllAccounts().isEmpty()) {
                accountDao.insertAccount(
                    Account(name = "Cash Pocket", initialBalance = 1500.0, colorHex = "#4CAF50") // Green
                )
                accountDao.insertAccount(
                    Account(name = "Bank Account", initialBalance = 5000.0, colorHex = "#2196F3") // Blue
                )
                accountDao.insertAccount(
                    Account(name = "Credit Card", initialBalance = 0.0, colorHex = "#FF9800") // Orange
                )
            }
        }
    }
}
