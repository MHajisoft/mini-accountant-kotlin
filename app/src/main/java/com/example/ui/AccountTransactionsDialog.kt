package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.Account
import com.example.data.AppLanguage
import com.example.data.formatByLang
import com.example.data.Transaction
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun AccountTransactionsDialog(
    account: Account,
    viewModel: FinanceViewModel,
    activeLang: AppLanguage,
    onDismiss: () -> Unit
) {
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var page by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var hasMore by remember { mutableStateOf(true) }
    val pageSize = 20

    LaunchedEffect(account.id, page) {
        if (hasMore && !isLoading) {
            isLoading = true
            viewModel.loadAccountTransactions(account.id, page, pageSize) { newItems ->
                if (newItems.isEmpty() || newItems.size < pageSize) {
                    hasMore = false
                }
                if (page == 0) {
                    transactions = newItems
                } else {
                    val currentIds = transactions.map { it.id }.toSet()
                    val toAdd = newItems.filter { it.id !in currentIds }
                    transactions = transactions + toAdd
                }
                isLoading = false
            }
        }
    }

    val listState = rememberLazyListState()
    
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filter { it != null && it >= transactions.size - 3 }
            .distinctUntilChanged()
            .collect {
                if (hasMore && !isLoading) {
                    page++
                }
            }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = account.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider()

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactions) { tx ->
                        // Render transaction here
                        TransactionItem(
                            tx = tx,
                            sourceAccountName = account.name,
                            targetAccountName = null, // Can fetch if needed but omitting for brevity
                            activeLang = activeLang,
                            onDelete = {
                                viewModel.deleteTransaction(tx)
                                // Remove from local list
                                transactions = transactions.filter { it.id != tx.id }
                            }
                        )
                    }
                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}
