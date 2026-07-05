package com.example.ui

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FinanceViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = FinanceRepository(database.accountDao(), database.transactionDao())

    private val sharedPrefs = application.getSharedPreferences("finance_prefs", Context.MODE_PRIVATE)

    // Language & Theme State
    private val _language = MutableStateFlow(AppLanguage.EN)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _theme = MutableStateFlow(AppTheme.VIBRANT)
    val theme: StateFlow<AppTheme> = _theme.asStateFlow()

    // Categories State (Fully Managed)
    private val _expenseCategories = MutableStateFlow<List<String>>(emptyList())
    val expenseCategories: StateFlow<List<String>> = _expenseCategories.asStateFlow()

    private val _incomeCategories = MutableStateFlow<List<String>>(emptyList())
    val incomeCategories: StateFlow<List<String>> = _incomeCategories.asStateFlow()

    // Database Observables
    val accounts: StateFlow<List<Account>> = repository.allAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cloud Sync States
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _lastSyncTime = MutableStateFlow(sharedPrefs.getLong("last_sync_time", 0L))
    val lastSyncTime: StateFlow<Long> = _lastSyncTime.asStateFlow()

    private val _isAutoSyncEnabled = MutableStateFlow(sharedPrefs.getBoolean("auto_sync_enabled", false))
    val isAutoSyncEnabled: StateFlow<Boolean> = _isAutoSyncEnabled.asStateFlow()

    init {
        // Load stored language
        val langCode = sharedPrefs.getString("app_lang", "en") ?: "en"
        _language.value = AppLanguage.values().find { it.code == langCode } ?: AppLanguage.EN

        // Load stored theme
        val themeName = sharedPrefs.getString("app_theme", "VIBRANT") ?: "VIBRANT"
        _theme.value = try {
            AppTheme.valueOf(themeName)
        } catch (e: Exception) {
            AppTheme.VIBRANT
        }

        // Load stored categories
        val storedExpense = sharedPrefs.getString("expense_categories", "Food,Groceries,Transport,Rent,Utilities,Shopping,Health,Education,Other") ?: ""
        _expenseCategories.value = storedExpense.split(",").filter { it.isNotEmpty() }

        val storedIncome = sharedPrefs.getString("income_categories", "Salary,Business,Investment,Gift,Other") ?: ""
        _incomeCategories.value = storedIncome.split(",").filter { it.isNotEmpty() }
    }

    // Category management
    fun addCategory(type: String, categoryName: String) {
        val trimmed = categoryName.trim()
        if (trimmed.isEmpty()) return
        if (type == "EXPENSE") {
            if (!_expenseCategories.value.contains(trimmed)) {
                val newList = _expenseCategories.value + trimmed
                _expenseCategories.value = newList
                sharedPrefs.edit().putString("expense_categories", newList.joinToString(",")).apply()
            }
        } else if (type == "INCOME") {
            if (!_incomeCategories.value.contains(trimmed)) {
                val newList = _incomeCategories.value + trimmed
                _incomeCategories.value = newList
                sharedPrefs.edit().putString("income_categories", newList.joinToString(",")).apply()
            }
        }
    }

    fun deleteCategory(type: String, categoryName: String) {
        if (type == "EXPENSE") {
            val newList = _expenseCategories.value.filter { it != categoryName }
            _expenseCategories.value = newList
            sharedPrefs.edit().putString("expense_categories", newList.joinToString(",")).apply()
        } else if (type == "INCOME") {
            val newList = _incomeCategories.value.filter { it != categoryName }
            _incomeCategories.value = newList
            sharedPrefs.edit().putString("income_categories", newList.joinToString(",")).apply()
        }
    }

    // Language controller
    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
        sharedPrefs.edit().putString("app_lang", lang.code).apply()
    }

    // Theme controller
    fun setTheme(newTheme: AppTheme) {
        _theme.value = newTheme
        sharedPrefs.edit().putString("app_theme", newTheme.name).apply()
    }

    // Accounts management
    fun addAccount(name: String, initialBalance: Double, colorHex: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAccount(Account(name = name, initialBalance = initialBalance, colorHex = colorHex))
        }
    }

    fun deleteAccount(account: Account) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAccount(account)
        }
    }

    // Transactions management
    fun addTransaction(
        amount: Double,
        type: String, // INCOME, EXPENSE, TRANSFER
        category: String,
        accountId: Int,
        transferToAccountId: Int? = null,
        description: String,
        timestamp: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTransaction(
                Transaction(
                    amount = amount,
                    type = type,
                    category = category,
                    accountId = accountId,
                    transferToAccountId = transferToAccountId,
                    timestamp = timestamp,
                    description = description
                )
            )

            // Auto-trigger cloud sync if enabled
            if (_isAutoSyncEnabled.value) {
                triggerCloudSync(silent = true)
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTransaction(transaction)

            // Auto-trigger cloud sync if enabled
            if (_isAutoSyncEnabled.value) {
                triggerCloudSync(silent = true)
            }
        }
    }

    // Helper to calculate individual account real-time balance
    fun getAccountBalance(account: Account, txList: List<Transaction>): Double {
        var balance = account.initialBalance
        for (tx in txList) {
            when (tx.type) {
                "INCOME" -> {
                    if (tx.accountId == account.id) {
                        balance += tx.amount
                    }
                }
                "EXPENSE" -> {
                    if (tx.accountId == account.id) {
                        balance -= tx.amount
                    }
                }
                "TRANSFER" -> {
                    if (tx.accountId == account.id) {
                        balance -= tx.amount
                    }
                    if (tx.transferToAccountId == account.id) {
                        balance += tx.amount
                    }
                }
            }
        }
        return balance
    }

    // Global net balance
    fun getNetBalance(accountsList: List<Account>, txList: List<Transaction>): Double {
        return accountsList.sumOf { getAccountBalance(it, txList) }
    }

    // Export JSON Backup String
    suspend fun exportBackupJson(): String {
        return repository.exportToJson()
    }

    // Import JSON Backup String
    fun importBackupJson(jsonString: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = repository.importFromJson(jsonString)
            viewModelScope.launch(Dispatchers.Main) {
                onComplete(success)
            }
        }
    }

    // Toggle Auto Sync
    fun setAutoSyncEnabled(enabled: Boolean) {
        _isAutoSyncEnabled.value = enabled
        sharedPrefs.edit().putBoolean("auto_sync_enabled", enabled).apply()
        if (enabled) {
            triggerCloudSync()
        }
    }

    // Google Drive / OneDrive simulated automated sync
    fun triggerCloudSync(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) _isSyncing.value = true
            // Simulate networking delay
            delay(1800)
            val now = System.currentTimeMillis()
            _lastSyncTime.value = now
            sharedPrefs.edit().putLong("last_sync_time", now).apply()
            _isSyncing.value = false
        }
    }
}
