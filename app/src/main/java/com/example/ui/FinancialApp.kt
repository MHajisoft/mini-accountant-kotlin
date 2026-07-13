package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.*
import com.example.ui.theme.AppTheme
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialApp(viewModel: FinanceViewModel = viewModel()) {
    val activeLang by viewModel.language.collectAsState()
    val activeTheme by viewModel.theme.collectAsState()

    val accountsList by viewModel.accounts.collectAsState()
    val transactionsList by viewModel.transactions.collectAsState()

    val expenseCategories by viewModel.expenseCategories.collectAsState()
    val incomeCategories by viewModel.incomeCategories.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var preselectedTransactionType by remember { mutableStateOf("EXPENSE") }

    // Dialog & Page flags
    var showAddAccountDialog by remember { mutableStateOf(false) }
    var showAddTransactionPage by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val layoutDirection = activeLang.layoutDirection

    // Wrap the entire visual tree in the localized Layout Direction (RTL for Persian/Arabic, LTR for English)
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        if (showAddTransactionPage) {
            AddTransactionPage(
                accounts = accountsList,
                activeLang = activeLang,
                initialType = preselectedTransactionType,
                expenseCategories = expenseCategories,
                incomeCategories = incomeCategories,
                onDismiss = { showAddTransactionPage = false },
                onSave = { amount, type, category, accountId, targetAccId, timestamp, description ->
                    viewModel.addTransaction(amount, type, category, accountId, targetAccId, description, timestamp)
                    showAddTransactionPage = false
                    Toast.makeText(context, Translations.getString("success", activeLang), Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                topBar = {
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        tonalElevation = 0.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left group: Circle Logo 'F' + Title & Sync info
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "F",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                }
                                Column {
                                    Text(
                                        text = "FinTrack",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF4CAF50)) // Synced dot (green)
                                        )
                                        Text(
                                            text = "Synced",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }

                            // Right group: language switcher + add account icon
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Quick Language Switcher Pill
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                        .clickable {
                                            // Cycle language
                                            val nextLang = when (activeLang) {
                                                AppLanguage.EN -> AppLanguage.FA
                                                AppLanguage.FA -> AppLanguage.AR
                                                AppLanguage.AR -> AppLanguage.EN
                                            }
                                            viewModel.setLanguage(nextLang)
                                        }
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = activeLang.code.uppercase(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Add Account Button Styled as profile-like action
                                IconButton(
                                    onClick = { showAddAccountDialog = true },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                        .testTag("add_account_top_btn")
                                ) {
                                    Icon(
                                        Icons.Default.AccountBalanceWallet,
                                        contentDescription = "Add Account",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 8.dp,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        val labelAccounts = Translations.getString("accounts", activeLang)
                        val labelTransactions = Translations.getString("transactions", activeLang)
                        val labelReports = Translations.getString("reports", activeLang)
                        val labelSettings = Translations.getString("settings", activeLang)

                        NavigationBarItem(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            icon = { Icon(if (selectedTab == 0) Icons.Filled.Dashboard else Icons.Outlined.Dashboard, contentDescription = labelAccounts) },
                            label = { Text(labelAccounts, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            modifier = Modifier.testTag("nav_tab_dashboard")
                        )
                        NavigationBarItem(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            icon = { Icon(if (selectedTab == 1) Icons.Filled.ListAlt else Icons.Outlined.ListAlt, contentDescription = labelTransactions) },
                            label = { Text(labelTransactions, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            modifier = Modifier.testTag("nav_tab_transactions")
                        )
                        NavigationBarItem(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            icon = { Icon(if (selectedTab == 2) Icons.Filled.PieChart else Icons.Outlined.PieChart, contentDescription = labelReports) },
                            label = { Text(labelReports, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            modifier = Modifier.testTag("nav_tab_reports")
                        )
                        NavigationBarItem(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            icon = { Icon(if (selectedTab == 3) Icons.Filled.Settings else Icons.Outlined.Settings, contentDescription = labelSettings) },
                            label = { Text(labelSettings, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            modifier = Modifier.testTag("nav_tab_settings")
                        )
                    }
                },
                floatingActionButton = {
                    if (selectedTab == 0 || selectedTab == 1) {
                        FloatingActionButton(
                            onClick = {
                                preselectedTransactionType = "EXPENSE"
                                showAddTransactionPage = true
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.testTag("fab_add_transaction")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                        }
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    // Render screen content based on selected tab
                    when (selectedTab) {
                        0 -> DashboardTab(
                            viewModel = viewModel,
                            accounts = accountsList,
                            transactions = transactionsList,
                            activeLang = activeLang,
                            onAddAccountClick = { showAddAccountDialog = true },
                            onQuickActionClick = { actionType ->
                                preselectedTransactionType = actionType
                                showAddTransactionPage = true
                            }
                        )
                        1 -> TransactionsTab(
                            viewModel = viewModel,
                            transactions = transactionsList,
                            accounts = accountsList,
                            activeLang = activeLang
                        )
                        2 -> ReportsTab(
                            accounts = accountsList,
                            transactions = transactionsList,
                            activeLang = activeLang
                        )
                        3 -> SettingsTab(
                            viewModel = viewModel,
                            activeLang = activeLang,
                            activeTheme = activeTheme
                        )
                    }
                }
            }
        }

        // Add Account Dialog
        if (showAddAccountDialog) {
            AddAccountDialog(
                activeLang = activeLang,
                onDismiss = { showAddAccountDialog = false },
                onSave = { name, balance, color ->
                    viewModel.addAccount(name, balance, color)
                    showAddAccountDialog = false
                    Toast.makeText(context, Translations.getString("success", activeLang), Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Add Transaction Dialog (Type Selection removed)
    }
}

// FORMATTER HELPERS
fun formatMoney(amount: Double, lang: AppLanguage): String {
    return if (lang == AppLanguage.FA) {
        val df = DecimalFormat("#,###")
        if (amount % 10.0 == 0.0) {
            df.format(amount / 10.0) + " تومان"
        } else {
            df.format(amount) + " ریال"
        }
    } else if (lang == AppLanguage.AR) {
        val df = DecimalFormat("#,##0.00")
        df.format(amount) + " د.إ"
    } else {
        val df = DecimalFormat("#,##0.00")
        "$" + df.format(amount)
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

// ------------------------------------
// TAB 0: DASHBOARD / ACCOUNTS SCREEN
// ------------------------------------
@Composable
fun DashboardTab(
    viewModel: FinanceViewModel,
    accounts: List<Account>,
    transactions: List<Transaction>,
    activeLang: AppLanguage,
    onAddAccountClick: () -> Unit,
    onQuickActionClick: (String) -> Unit
) {
    var selectedAccountForTransactions by remember { mutableStateOf<Account?>(null) }
    val netBalance = viewModel.getNetBalance(accounts, transactions)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Global Net Balance Banner (Vibrant Design)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("net_balance_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = Translations.getString("total_balance", activeLang),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = formatMoney(netBalance, activeLang),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            letterSpacing = (-1).sp
                        )
                        Text(
                            text = if (netBalance >= 0) "+2.4%" else "-1.5%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (netBalance >= 0) Color(0xFF0061A4) else Color(0xFFE91E63)
                        )
                    }
                    
                    // Budget Progress bar
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.4f))
                    ) {
                        val fraction = remember(netBalance) {
                            if (netBalance <= 0) 0.05f
                            else if (netBalance >= 18000.0) 1.0f
                            else (netBalance / 18000.0).toFloat()
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = fraction)
                                .fillMaxHeight()
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Monthly Budget: $18,000",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Quick Actions Grid (Vibrant Design Layout)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Income Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFE8F5E9)) // green-100
                            .clickable {
                                onQuickActionClick("INCOME")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("↓", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Translations.getString("income", activeLang),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }

                // Cost Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFFEBEE)) // red-100
                            .clickable {
                                onQuickActionClick("EXPENSE")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("↑", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Translations.getString("expense", activeLang),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }

                // Transfer Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFFF3E0)) // orange-100
                            .clickable {
                                onQuickActionClick("TRANSFER")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⇆", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF6C00))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Translations.getString("transfer", activeLang),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // Accounts Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Translations.getString("accounts", activeLang),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = onAddAccountClick) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(Translations.getString("add_account_fab", activeLang))
                }
            }
        }

        // Empty accounts notice
        if (accounts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = Translations.getString("no_accounts", activeLang),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(accounts) { account ->
                val currentBalance = viewModel.getAccountBalance(account, transactions)
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = try {
                                Color(android.graphics.Color.parseColor(account.colorHex))
                            } catch (e: Exception) {
                                MaterialTheme.colorScheme.primary
                            },
                            shape = RoundedCornerShape(16.dp)
                        )
                        .testTag("account_card_${account.id}")
                        .clickable { selectedAccountForTransactions = account },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(
                                            try {
                                                Color(android.graphics.Color.parseColor(account.colorHex))
                                            } catch (e: Exception) {
                                                MaterialTheme.colorScheme.primary
                                            }
                                        )
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = account.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }


                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = Translations.getString("balance", activeLang),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatMoney(currentBalance, activeLang),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (currentBalance >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        // Recent Transaction Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = Translations.getString("recent_transactions", activeLang),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (transactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = Translations.getString("no_transactions", activeLang),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            // Take the first 5 transactions for the dashboard
            items(transactions.take(5)) { tx ->
                val sourceAccount = accounts.find { it.id == tx.accountId }
                val targetAccount = if (tx.type == "TRANSFER") accounts.find { it.id == tx.transferToAccountId } else null

                TransactionItem(
                    tx = tx,
                    sourceAccountName = sourceAccount?.name ?: "Unknown",
                    targetAccountName = targetAccount?.name,
                    activeLang = activeLang,
                    onDelete = { viewModel.deleteTransaction(tx) }
                )
            }
        }
    }
    
    selectedAccountForTransactions?.let { account ->
        AccountTransactionsDialog(
            account = account,
            viewModel = viewModel,
            activeLang = activeLang,
            onDismiss = { selectedAccountForTransactions = null }
        )
    }
}

// ------------------------------------
// TAB 1: TRANSACTIONS LIST LEDGER
// ------------------------------------
@Composable
fun TransactionsTab(
    viewModel: FinanceViewModel,
    transactions: List<Transaction>,
    accounts: List<Account>,
    activeLang: AppLanguage
) {
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, INCOME, EXPENSE, TRANSFER

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Filter Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf("ALL", "INCOME", "EXPENSE", "TRANSFER")
            filters.forEach { filter ->
                val label = when (filter) {
                    "ALL" -> if (activeLang == AppLanguage.EN) "All" else if (activeLang == AppLanguage.FA) "همه" else "الكل"
                    "INCOME" -> Translations.getString("income", activeLang)
                    "EXPENSE" -> Translations.getString("expense", activeLang)
                    else -> Translations.getString("transfer", activeLang)
                }

                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(label, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Filtered transactions list
        val filteredTransactions = remember(transactions, selectedFilter) {
            if (selectedFilter == "ALL") {
                transactions
            } else {
                transactions.filter { it.type == selectedFilter }
            }
        }

        if (filteredTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = Translations.getString("no_transactions", activeLang),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredTransactions) { tx ->
                    val sourceAccount = accounts.find { it.id == tx.accountId }
                    val targetAccount = if (tx.type == "TRANSFER") accounts.find { it.id == tx.transferToAccountId } else null

                    TransactionItem(
                        tx = tx,
                        sourceAccountName = sourceAccount?.name ?: "Unknown",
                        targetAccountName = targetAccount?.name,
                        activeLang = activeLang,
                        onDelete = { viewModel.deleteTransaction(tx) }
                    )
                }
            }
        }
    }
}

// TRANSACTION ROW COMPONENT
@Composable
fun TransactionItem(
    tx: Transaction,
    sourceAccountName: String,
    targetAccountName: String?,
    activeLang: AppLanguage,
    onDelete: () -> Unit
) {
    
    var confirmDelete by remember { mutableStateOf(false) }
    val categoryBgColor = when {
        tx.type == "INCOME" -> Color(0xFFE8F5E9) // Emerald-100
        tx.type == "TRANSFER" -> Color(0xFFF3E5F5) // Purple-100
        tx.category in listOf("Food", "Groceries") -> Color(0xFFFFF8E1) // Amber-100
        tx.category in listOf("Transport", "Utilities", "Rent") -> Color(0xFFE1F5FE) // Blue-100
        else -> Color(0xFFECEFF1) // Gray-100 fallback
    }
    val categoryIconColor = when {
        tx.type == "INCOME" -> Color(0xFF2E7D32)
        tx.type == "TRANSFER" -> Color(0xFF6A1B9A)
        tx.category in listOf("Food", "Groceries") -> Color(0xFFFF8F00)
        tx.category in listOf("Transport", "Utilities", "Rent") -> Color(0xFF0288D1)
        else -> Color(0xFF546E7A)
    }
    val categoryEmojiOrIcon = when {
        tx.type == "INCOME" -> "💰"
        tx.type == "TRANSFER" -> "⇆"
        tx.category in listOf("Food", "Groceries") -> "🛒"
        tx.category == "Shopping" -> "🛍️"
        tx.category == "Transport" -> "🚗"
        tx.category == "Rent" -> "🏠"
        tx.category == "Utilities" -> "⚡"
        else -> "💳"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("tx_item_${tx.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(categoryBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(categoryEmojiOrIcon, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val categoryTranslated = translateCategory(tx.category, activeLang)
                    Text(
                        text = categoryTranslated,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = (if (tx.type == "EXPENSE") "-" else if (tx.type == "INCOME") "+" else "") + formatMoney(tx.amount, activeLang),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = categoryIconColor
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val subtitle = when (tx.type) {
                        "TRANSFER" -> "$sourceAccountName ➔ ${targetAccountName ?: "?"}"
                        else -> sourceAccountName
                    }
                    Text(
                        text = subtitle + if (tx.description.isNotEmpty()) " (${tx.description})" else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(
                        text = formatDate(tx.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }
            }

            // Delete item action
            Spacer(modifier = Modifier.width(8.dp))
            if (!confirmDelete) {
                IconButton(
                    onClick = { confirmDelete = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Delete transaction",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { confirmDelete = false },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Cancel", modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = {
                            onDelete()
                            confirmDelete = false
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Confirm", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// ------------------------------------
// TAB 2: REPORTS & DIAGRAMS SCREEN
// ------------------------------------
@Composable
fun ReportsTab(
    accounts: List<Account>,
    transactions: List<Transaction>,
    activeLang: AppLanguage
) {
    // 1. Calculate stats
    val totalIncome = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }

    // 2. Calculate categories stats for expenses
    val expenseTransactions = transactions.filter { it.type == "EXPENSE" }
    val expenseByCategory = remember(expenseTransactions) {
        expenseTransactions.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Income vs Expense Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reports_summary_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = Translations.getString("income_vs_expense", activeLang),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Dynamic bar drawing
                    val maxVal = maxOf(totalIncome, totalExpense, 1.0)
                    val incomeRatio = (totalIncome / maxVal).toFloat()
                    val expenseRatio = (totalExpense / maxVal).toFloat()

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Income Row
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(Translations.getString("income", activeLang), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(formatMoney(totalIncome, activeLang), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(16.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = maxOf(0.02f, incomeRatio))
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF4CAF50))
                                )
                            }
                        }

                        // Expense Row
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(Translations.getString("expense", activeLang), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(formatMoney(totalExpense, activeLang), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE91E63))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(16.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = maxOf(0.02f, expenseRatio))
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFE91E63))
                                )
                            }
                        }
                    }
                }
            }
        }

        // Custom Radial Pie Chart Canvas Drawing!
        if (totalExpense > 0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = Translations.getString("expenses_by_category", activeLang),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // Draw Pie Chart
                        Box(
                            modifier = Modifier.size(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val colorPalette = listOf(
                                Color(0xFFE91E63), // Pink
                                Color(0xFF2196F3), // Blue
                                Color(0xFFFF9800), // Orange
                                Color(0xFF9C27B0), // Purple
                                Color(0xFF4CAF50), // Green
                                Color(0xFF00BCD4), // Teal
                                Color(0xFFFFEB3B), // Yellow
                                Color(0xFF795548)  // Brown
                            )

                            Canvas(modifier = Modifier.fillMaxSize()) {
                                var startAngle = 0f
                                expenseByCategory.forEachIndexed { idx, item ->
                                    val sweep = (item.second / totalExpense * 360f).toFloat()
                                    val color = colorPalette[idx % colorPalette.size]

                                    drawArc(
                                        color = color,
                                        startAngle = startAngle,
                                        sweepAngle = sweep,
                                        useCenter = false,
                                        style = Stroke(width = 30.dp.toPx())
                                    )
                                    startAngle += sweep
                                }
                            }

                            // Show Center Percentage
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "100%",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = Translations.getString("expense", activeLang),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Category list with indicators
                        val colorPalette = listOf(
                            Color(0xFFE91E63),
                            Color(0xFF2196F3),
                            Color(0xFFFF9800),
                            Color(0xFF9C27B0),
                            Color(0xFF4CAF50),
                            Color(0xFF00BCD4),
                            Color(0xFFFFEB3B),
                            Color(0xFF795548)
                        )

                        expenseByCategory.forEachIndexed { idx, item ->
                            val color = colorPalette[idx % colorPalette.size]
                            val percent = (item.second / totalExpense * 100).toInt()

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = translateCategory(item.first, activeLang),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Row {
                                    Text(
                                        text = "$percent%  ",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = formatMoney(item.second, activeLang),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = Translations.getString("no_transactions", activeLang),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// ------------------------------------
// TAB 3: SETTINGS, THEME, LANGUAGE, BACKUP
// ------------------------------------
@Composable
fun SettingsTab(
    viewModel: FinanceViewModel,
    activeLang: AppLanguage,
    activeTheme: AppTheme
) {
    val isSyncing by viewModel.isSyncing.collectAsState()
    val lastSyncTime by viewModel.lastSyncTime.collectAsState()
    val isAutoSyncEnabled by viewModel.isAutoSyncEnabled.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Multi-Language Panel
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("language_settings_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = Translations.getString("language", activeLang),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    AppLanguage.values().forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setLanguage(lang) }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lang.displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (activeLang == lang) FontWeight.Bold else FontWeight.Normal,
                                color = if (activeLang == lang) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            if (activeLang == lang) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Multi-Theme Selector Panel
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("theme_settings_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = Translations.getString("theme", activeLang),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Grid or Row of Themes
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppTheme.values().toList().chunked(4).forEach { rowThemes ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowThemes.forEach { themeItem ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (activeTheme == themeItem) MaterialTheme.colorScheme.primaryContainer
                                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                            )
                                            .border(
                                                width = if (activeTheme == themeItem) 2.dp else 1.dp,
                                                color = if (activeTheme == themeItem) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable { viewModel.setTheme(themeItem) }
                                            .padding(10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = themeItem.displayName,
                                            fontSize = 12.sp,
                                            fontWeight = if (activeTheme == themeItem) FontWeight.Bold else FontWeight.Normal,
                                            color = if (activeTheme == themeItem) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Backup and Restore (JSON Local & Cloud Sync)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("backup_settings_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = Translations.getString("backup_restore", activeLang),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Export JSON Option
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                val jsonStr = viewModel.exportBackupJson()
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("FinanceTrackerBackup", jsonStr)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(
                                    context,
                                    Translations.getString("export_backup", activeLang) + " ➔ " + Translations.getString("synchronized", activeLang) + " (Copied)",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(Translations.getString("export_backup", activeLang))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Import JSON Dialog Trigger
                    var showImportInputState by remember { mutableStateOf(false) }
                    var jsonPasteText by remember { mutableStateOf("") }

                    OutlinedButton(
                        onClick = { showImportInputState = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(Translations.getString("import_backup", activeLang))
                    }

                    if (showImportInputState) {
                        Dialog(onDismissRequest = { showImportInputState = false }) {
                            Card(
                                modifier = Modifier.padding(16.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        Translations.getString("import_backup", activeLang),
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = jsonPasteText,
                                        onValueChange = { jsonPasteText = it },
                                        label = { Text("Paste JSON Backup") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp),
                                        maxLines = 10
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(onClick = { showImportInputState = false }) {
                                            Text(Translations.getString("cancel", activeLang))
                                        }
                                        Button(
                                            onClick = {
                                                viewModel.importBackupJson(jsonPasteText) { success ->
                                                    showImportInputState = false
                                                    jsonPasteText = ""
                                                    val msgKey = if (success) "synchronized" else "delete"
                                                    Toast.makeText(context, Translations.getString(msgKey, activeLang), Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        ) {
                                            Text(Translations.getString("save", activeLang))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Cloud Auto Sync Panel
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = Translations.getString("sync_cloud", activeLang),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = Translations.getString("sync_desc", activeLang),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isAutoSyncEnabled,
                            onCheckedChange = { viewModel.setAutoSyncEnabled(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sync action button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (lastSyncTime > 0) {
                                val dateStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(lastSyncTime))
                                "Last synced: $dateStr"
                            } else "Not synced yet",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )

                        Button(
                            onClick = { viewModel.triggerCloudSync() },
                            enabled = !isSyncing
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(Translations.getString("syncing", activeLang))
                            } else {
                                Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(Translations.getString("sync_now", activeLang))
                            }
                        }
                    }
                }
            }
        }

        // Category Management Panel
        item {
            var activeCatType by remember { mutableStateOf("EXPENSE") } // "EXPENSE" or "INCOME"
            val expenseCats by viewModel.expenseCategories.collectAsState()
            val incomeCats by viewModel.incomeCategories.collectAsState()
            var newCatName by remember { mutableStateOf("") }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("categories_management_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (activeLang == AppLanguage.FA) "مدیریت دسته‌بندی‌ها" else if (activeLang == AppLanguage.AR) "إدارة الفئات" else "Manage Categories",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Selector for Expense / Income
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("EXPENSE", "INCOME").forEach { type ->
                            val label = if (type == "EXPENSE") {
                                if (activeLang == AppLanguage.FA) "هزینه‌ها" else if (activeLang == AppLanguage.AR) "النفقات" else "Expenses"
                            } else {
                                if (activeLang == AppLanguage.FA) "درآمدها" else if (activeLang == AppLanguage.AR) "الدخل" else "Income"
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (activeCatType == type) MaterialTheme.colorScheme.primary
                                        else Color.Transparent
                                    )
                                    .clickable { activeCatType = type }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (activeCatType == type) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Current categories list
                    val currentCats = if (activeCatType == "EXPENSE") expenseCats else incomeCats
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        currentCats.forEach { cat ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = translateCategory(cat, activeLang),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                IconButton(
                                    onClick = { viewModel.deleteCategory(activeCatType, cat) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Add new category input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newCatName,
                            onValueChange = { newCatName = it },
                            placeholder = {
                                Text(
                                    text = if (activeLang == AppLanguage.FA) "نام دسته‌بندی جدید" else if (activeLang == AppLanguage.AR) "اسم فئة جديدة" else "New category name",
                                    fontSize = 12.sp
                                )
                            },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )
                        Button(
                            onClick = {
                                if (newCatName.trim().isNotEmpty()) {
                                    viewModel.addCategory(activeCatType, newCatName.trim())
                                    newCatName = ""
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

// ------------------------------------
// DIALOG: ADD NEW ACCOUNT
// ------------------------------------
@Composable
fun AddAccountDialog(
    activeLang: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (name: String, balance: Double, colorHex: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("") }

    val colorList = listOf("#4CAF50", "#2196F3", "#FF9800", "#E91E63", "#9C27B0", "#00BCD4")
    var selectedColor by remember { mutableStateOf(colorList[0]) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("add_account_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = Translations.getString("add_account", activeLang),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(Translations.getString("account_name", activeLang)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("account_name_input")
                )

                OutlinedTextField(
                    value = balance,
                    onValueChange = { balance = it.replace(Regex("[^\\d.]"), "") },
                    label = { Text(Translations.getString("initial_balance", activeLang)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    visualTransformation = ThousandsSeparatorVisualTransformation(),
                    modifier = Modifier.fillMaxWidth().testTag("account_balance_input")
                )

                // Color picker
                Column {
                    Text(
                        text = "Card Theme Color",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        colorList.forEach { colorHex ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(colorHex)))
                                    .border(
                                        width = if (selectedColor == colorHex) 3.dp else 0.dp,
                                        color = if (selectedColor == colorHex) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColor = colorHex }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(Translations.getString("cancel", activeLang))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val bal = balance.toDoubleOrNull() ?: 0.0
                            if (name.isNotEmpty()) {
                                onSave(name, bal, selectedColor)
                            }
                        },
                        enabled = name.isNotEmpty()
                    ) {
                        Text(Translations.getString("save", activeLang))
                    }
                }
            }
        }
    }
}

