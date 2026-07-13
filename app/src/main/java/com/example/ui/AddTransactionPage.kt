package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import java.util.Calendar

fun translateCategory(category: String, lang: AppLanguage): String {
    val translated = Translations.getString(category.replace(" ", "_").lowercase(), lang)
    if (translated == category.replace(" ", "_").lowercase()) {
        return category
    }
    return translated
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionPage(
    accounts: List<Account>,
    activeLang: AppLanguage,
    initialType: String,
    expenseCategories: List<String>,
    incomeCategories: List<String>,
    onDismiss: () -> Unit,
    onSave: (amount: Double, type: String, category: String, accountId: Int, targetAccId: Int?, timestamp: Long, description: String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var currentType by remember { mutableStateOf(if (initialType == "VAULT") "EXPENSE" else initialType) }
    
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: -1) }
    var selectedTargetAccountId by remember { mutableStateOf(accounts.getOrNull(1)?.id ?: accounts.firstOrNull()?.id ?: -1) }
    
    val currentCategories = if (currentType == "EXPENSE") expenseCategories else incomeCategories
    var selectedCategory by remember { mutableStateOf(currentCategories.firstOrNull() ?: "Other") }
    
    var selectedTimestamp by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    var showSourceAccountSheet by remember { mutableStateOf(false) }
    var showTargetAccountSheet by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }
    
    val sourceSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val targetSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val categorySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(currentType) {
        val newCategories = if (currentType == "EXPENSE") expenseCategories else incomeCategories
        if (selectedCategory !in newCategories) {
            selectedCategory = newCategories.firstOrNull() ?: "Other"
        }
    }

    if (showDatePicker) {
        CultureDatePickerDialog(
            initialTimestamp = selectedTimestamp,
            lang = activeLang,
            onDismiss = { showDatePicker = false },
            onDateSelected = {
                selectedTimestamp = it
                showDatePicker = false
            }
        )
    }
    
    if (showSourceAccountSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSourceAccountSheet = false },
            sheetState = sourceSheetState
        ) {
            AccountSelectionList(
                accounts = accounts,
                activeLang = activeLang,
                selectedId = selectedAccountId,
                onSelect = { 
                    selectedAccountId = it.id
                    showSourceAccountSheet = false 
                }
            )
        }
    }

    if (showTargetAccountSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTargetAccountSheet = false },
            sheetState = targetSheetState
        ) {
            AccountSelectionList(
                accounts = accounts.filter { it.id != selectedAccountId },
                activeLang = activeLang,
                selectedId = selectedTargetAccountId,
                onSelect = { 
                    selectedTargetAccountId = it.id
                    showTargetAccountSheet = false 
                }
            )
        }
    }

    if (showCategorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showCategorySheet = false },
            sheetState = categorySheetState
        ) {
            CategorySelectionList(
                categories = currentCategories,
                activeLang = activeLang,
                selectedCategory = selectedCategory,
                onSelect = {
                    selectedCategory = it
                    showCategorySheet = false
                }
            )
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(shadowElevation = 4.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = Translations.getString("add_transaction", activeLang),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(Translations.getString("cancel", activeLang), fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            val amt = amount.toDoubleOrNull() ?: 0.0
                            if (amt > 0) {
                                onSave(amt, currentType, selectedCategory, selectedAccountId, if (currentType == "TRANSFER") selectedTargetAccountId else null, selectedTimestamp, description)
                            }
                        }
                    ) {
                        Text(Translations.getString("save", activeLang), fontSize = 16.sp)
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = currentType == "EXPENSE",
                        onClick = { currentType = "EXPENSE" },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                    ) { Text(Translations.getString("expense", activeLang)) }
                    SegmentedButton(
                        selected = currentType == "INCOME",
                        onClick = { currentType = "INCOME" },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                    ) { Text(Translations.getString("income", activeLang)) }
                    SegmentedButton(
                        selected = currentType == "TRANSFER",
                        onClick = { currentType = "TRANSFER" },
                        shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                    ) { Text(Translations.getString("transfer", activeLang)) }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = Translations.getString("amount", activeLang).uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it.replace(Regex("[^\\d.]"), "") },
                            placeholder = { Text("0", style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))) },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            visualTransformation = ThousandsSeparatorVisualTransformation(),
                            modifier = Modifier.fillMaxWidth().testTag("transaction_amount_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                disabledBorderColor = Color.Transparent
                            )
                        )
                    }
                }
            }

            item {
                val selectedAcc = accounts.find { it.id == selectedAccountId } ?: accounts.firstOrNull()
                SelectionRow(
                    label = Translations.getString(if (currentType == "TRANSFER") "from_account" else "select_account", activeLang),
                    value = selectedAcc?.name ?: "Select",
                    colorHex = selectedAcc?.colorHex ?: "#1a73e8",
                    icon = Icons.Default.AccountBalanceWallet,
                    onClick = { showSourceAccountSheet = true }
                )
            }

            if (currentType == "TRANSFER") {
                item {
                    val selectedAcc = accounts.find { it.id == selectedTargetAccountId } ?: accounts.firstOrNull()
                    SelectionRow(
                        label = Translations.getString("to_account", activeLang),
                        value = selectedAcc?.name ?: "Select",
                        colorHex = selectedAcc?.colorHex ?: "#1a73e8",
                        icon = Icons.Default.AccountBalanceWallet,
                        onClick = { showTargetAccountSheet = true }
                    )
                }
            }

            if (currentType != "TRANSFER") {
                item {
                    val catTranslated = translateCategory(selectedCategory, activeLang)
                    SelectionRow(
                        label = Translations.getString("select_category", activeLang),
                        value = catTranslated,
                        colorHex = "#9C27B0", // Purple
                        icon = Icons.Default.Category,
                        onClick = { showCategorySheet = true }
                    )
                }
            }

            item {
                val dateStr = formatCultureDate(selectedTimestamp, activeLang)
                SelectionRow(
                    label = Translations.getString("date", activeLang),
                    value = dateStr,
                    colorHex = "#FF9800",
                    icon = Icons.Default.CalendarToday,
                    onClick = { showDatePicker = true }
                )
            }
            
            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(Translations.getString("description", activeLang)) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().testTag("transaction_desc_input")
                )
            }
            

        }
    }
}

@Composable
fun SelectionRow(
    label: String,
    value: String,
    colorHex: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    val accColor = try { Color(android.graphics.Color.parseColor(colorHex)) } catch (e: Exception) { MaterialTheme.colorScheme.primary }
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(accColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = accColor, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
            }
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun AccountSelectionList(accounts: List<Account>, activeLang: AppLanguage, selectedId: Int, onSelect: (Account) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).padding(bottom = 32.dp)) {
        Text(Translations.getString("select_account", activeLang), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
        accounts.forEach { acc ->
            val isCurrent = selectedId == acc.id
            val cColor = try { Color(android.graphics.Color.parseColor(acc.colorHex)) } catch (e: Exception) { MaterialTheme.colorScheme.primary }
            Surface(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onSelect(acc) },
                shape = RoundedCornerShape(12.dp),
                color = if (isCurrent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent,
                border = BorderStroke(width = if (isCurrent) 1.5.dp else 1.dp, color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(cColor.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = cColor, modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text(acc.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (isCurrent) {
                        Icon(Icons.Default.Check, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CategorySelectionList(categories: List<String>, activeLang: AppLanguage, selectedCategory: String, onSelect: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).padding(bottom = 32.dp)) {
        Text(Translations.getString("select_category", activeLang), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
        categories.chunked(3).forEach { rowCats ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowCats.forEach { cat ->
                    val catTranslated = translateCategory(cat, activeLang)
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .border(width = 1.5.dp, color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent, shape = RoundedCornerShape(12.dp))
                            .clickable { onSelect(cat) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(catTranslated, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                    }
                }
            }
        }
    }
}

fun formatCultureDate(timestamp: Long, lang: AppLanguage): String {
    val isJalali = lang == AppLanguage.FA
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
    return if (isJalali) {
        val jDate = JalaliCalendarHelper.gregorianToJalali(JalaliCalendarHelper.GregorianDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)))
        "${jDate.year}/${jDate.month}/${jDate.day}"
    } else {
        "${cal.get(Calendar.YEAR)}/${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.DAY_OF_MONTH)}"
    }
}

@Composable
fun CultureDatePickerDialog(initialTimestamp: Long, lang: AppLanguage, onDismiss: () -> Unit, onDateSelected: (Long) -> Unit) {
    val isJalali = lang == AppLanguage.FA
    val initialDate = remember(initialTimestamp) {
        val cal = Calendar.getInstance().apply { timeInMillis = initialTimestamp }
        if (isJalali) {
            val j = JalaliCalendarHelper.gregorianToJalali(JalaliCalendarHelper.GregorianDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)))
            Triple(j.year, j.month, j.day)
        } else {
            Triple(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
        }
    }
    
    var year by remember { mutableStateOf(initialDate.first) }
    var month by remember { mutableStateOf(initialDate.second) }
    var day by remember { mutableStateOf(initialDate.third) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(Translations.getString("date", lang), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { year++ }) { Icon(Icons.Default.KeyboardArrowUp, null) }
                        Text("$year")
                        IconButton(onClick = { year-- }) { Icon(Icons.Default.KeyboardArrowDown, null) }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { month = if(month == 12) 1 else month + 1 }) { Icon(Icons.Default.KeyboardArrowUp, null) }
                        Text("$month")
                        IconButton(onClick = { month = if(month == 1) 12 else month - 1 }) { Icon(Icons.Default.KeyboardArrowDown, null) }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { day = if(day == 31) 1 else day + 1 }) { Icon(Icons.Default.KeyboardArrowUp, null) }
                        Text("$day")
                        IconButton(onClick = { day = if(day == 1) 31 else day - 1 }) { Icon(Icons.Default.KeyboardArrowDown, null) }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(Translations.getString("cancel", lang)) }
                    Button(onClick = {
                        if (isJalali) {
                            val gDate = JalaliCalendarHelper.jalaliToGregorian(JalaliCalendarHelper.JalaliDate(year, month, day))
                            val cal = Calendar.getInstance().apply { set(gDate.year, gDate.month - 1, gDate.day) }
                            onDateSelected(cal.timeInMillis)
                        } else {
                            val cal = Calendar.getInstance().apply { set(year, month - 1, day) }
                            onDateSelected(cal.timeInMillis)
                        }
                    }) { Text(Translations.getString("save", lang)) }
                }
            }
        }
    }
}
