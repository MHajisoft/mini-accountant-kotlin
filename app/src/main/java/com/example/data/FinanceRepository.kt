package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

class FinanceRepository(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao
) {
    val allAccounts: Flow<List<Account>> = accountDao.getAllAccountsFlow()
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactionsFlow()

    fun getTransactionsByAccount(accountId: Int): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByAccountFlow(accountId)
    }

    suspend fun insertAccount(account: Account): Long {
        return accountDao.insertAccount(account)
    }

    suspend fun updateAccount(account: Account) {
        accountDao.updateAccount(account)
    }

    suspend fun deleteAccount(account: Account) {
        // Delete all transactions of this account first
        transactionDao.deleteTransactionsByAccountId(account.id)
        accountDao.deleteAccount(account)
    }

    suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }

    // Export entire database as a JSON String
    suspend fun exportToJson(): String {
        val rootJson = JSONObject()

        // 1. Export Accounts
        val accountsList = accountDao.getAllAccounts()
        val accountsArray = JSONArray()
        for (account in accountsList) {
            val accJson = JSONObject().apply {
                put("id", account.id)
                put("name", account.name)
                put("initialBalance", account.initialBalance)
                put("colorHex", account.colorHex)
            }
            accountsArray.put(accJson)
        }
        rootJson.put("accounts", accountsArray)

        // 2. Export Transactions
        val transactionsList = transactionDao.getAllTransactions()
        val transactionsArray = JSONArray()
        for (tx in transactionsList) {
            val txJson = JSONObject().apply {
                put("id", tx.id)
                put("amount", tx.amount)
                put("type", tx.type)
                put("category", tx.category)
                put("accountId", tx.accountId)
                put("transferToAccountId", tx.transferToAccountId ?: JSONObject.NULL)
                put("timestamp", tx.timestamp)
                put("description", tx.description)
            }
            transactionsArray.put(txJson)
        }
        rootJson.put("transactions", transactionsArray)

        return rootJson.toString(2) // Pretty printed with 2 indent spaces
    }

    // Import and restore from a JSON string
    suspend fun importFromJson(jsonString: String): Boolean {
        return try {
            val rootJson = JSONObject(jsonString)

            // Validate that we have accounts and transactions
            if (!rootJson.has("accounts")) return false

            val accountsArray = rootJson.getJSONArray("accounts")
            val transactionsArray = rootJson.optJSONArray("transactions") ?: JSONArray()

            // 1. Temporarily fetch existing accounts or we can just clear existing and replace
            // For a complete restore, we clear the DB first
            val currentAccounts = accountDao.getAllAccounts()
            for (acc in currentAccounts) {
                transactionDao.deleteTransactionsByAccountId(acc.id)
                accountDao.deleteAccount(acc)
            }

            // 2. Import Accounts
            for (i in 0 until accountsArray.length()) {
                val accJson = accountsArray.getJSONObject(i)
                val account = Account(
                    id = accJson.optInt("id", 0),
                    name = accJson.getString("name"),
                    initialBalance = accJson.getDouble("initialBalance"),
                    colorHex = accJson.optString("colorHex", "#2196F3")
                )
                accountDao.insertAccount(account)
            }

            // 3. Import Transactions
            for (i in 0 until transactionsArray.length()) {
                val txJson = transactionsArray.getJSONObject(i)
                val transferToAcc = if (txJson.isNull("transferToAccountId")) null else txJson.getInt("transferToAccountId")
                val tx = Transaction(
                    id = txJson.optInt("id", 0),
                    amount = txJson.getDouble("amount"),
                    type = txJson.getString("type"),
                    category = txJson.optString("category", "Other"),
                    accountId = txJson.getInt("accountId"),
                    transferToAccountId = transferToAcc,
                    timestamp = txJson.getLong("timestamp"),
                    description = txJson.optString("description", "")
                )
                transactionDao.insertTransaction(tx)
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
