import re

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "r") as f:
    content = f.read()

# Replace initialType usage inside AddTransactionPage
def replace_in_add_transaction_page(match):
    body = match.group(0)
    
    # 1. Add currentType state
    body = body.replace(
        'var amount by remember { mutableStateOf("") }',
        'var currentType by remember { mutableStateOf(initialType) }\n    var amount by remember { mutableStateOf("") }'
    )
    
    # 2. Update LaunchedEffect
    body = body.replace(
        'LaunchedEffect(initialType, expenseCategories, incomeCategories) {\n        selectedCategory = when (initialType) {',
        'LaunchedEffect(currentType, expenseCategories, incomeCategories) {\n        selectedCategory = when (currentType) {'
    )
    
    # 3. Update Title to static string (e.g., "Add Transaction")
    body = body.replace(
        'text = getPageTitleForType(initialType, activeLang)',
        'text = Translations.getString("add_transaction", activeLang)'
    )
    
    # 4. Update onSave
    body = body.replace(
        'val finalTargetId = if (initialType == "TRANSFER") selectedTargetAccountId else null\n                                onSave(amt, initialType, selectedCategory, selectedAccountId, finalTargetId, selectedTimestamp, description)',
        'val finalTargetId = if (currentType == "TRANSFER") selectedTargetAccountId else null\n                                onSave(amt, currentType, selectedCategory, selectedAccountId, finalTargetId, selectedTimestamp, description)'
    )
    
    # 5. Add SegmentedButton at the top of LazyColumn
    segmented_button_ui = """            item {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SegmentedButton(
                        selected = currentType == "EXPENSE",
                        onClick = { currentType = "EXPENSE" },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                    ) {
                        Text(if (activeLang == AppLanguage.FA) "هزینه" else if (activeLang == AppLanguage.AR) "مصروف" else "Expense")
                    }
                    SegmentedButton(
                        selected = currentType == "INCOME",
                        onClick = { currentType = "INCOME" },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                    ) {
                        Text(if (activeLang == AppLanguage.FA) "درآمد" else if (activeLang == AppLanguage.AR) "دخل" else "Income")
                    }
                    SegmentedButton(
                        selected = currentType == "TRANSFER",
                        onClick = { currentType = "TRANSFER" },
                        shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                    ) {
                        Text(if (activeLang == AppLanguage.FA) "انتقال" else if (activeLang == AppLanguage.AR) "تحويل" else "Transfer")
                    }
                }
            }"""
    
    body = body.replace(
        '            // 1. Large Amount Input field',
        segmented_button_ui + '\n            // 1. Large Amount Input field'
    )
    
    # 6. Change initialType references
    body = body.replace('if (initialType == "TRANSFER")', 'if (currentType == "TRANSFER")')
    body = body.replace('if (initialType != "TRANSFER")', 'if (currentType != "TRANSFER")')
    body = body.replace('initialType == "EXPENSE"', 'currentType == "EXPENSE"')
    
    return body

# We need to find AddTransactionPage function body
pattern = r'@Composable\nfun AddTransactionPage\([\s\S]*?(?=\n@Composable|\nfun getPageTitleForType)'
content = re.sub(pattern, replace_in_add_transaction_page, content)

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "w") as f:
    f.write(content)
