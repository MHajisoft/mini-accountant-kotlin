import re

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "r") as f:
    content = f.read()

# Add selectedAccountForTransactions state to DashboardTab
content = content.replace(
    'val netBalance = viewModel.getNetBalance(accounts, transactions)',
    'var selectedAccountForTransactions by remember { mutableStateOf<Account?>(null) }\n    val netBalance = viewModel.getNetBalance(accounts, transactions)'
)

# Render AccountTransactionsDialog at the end of DashboardTab
dashboard_end_pattern = r'            }\n        }\n    }\n}'

new_dashboard_end = """            }
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
}"""

content = re.sub(dashboard_end_pattern, new_dashboard_end, content, count=1)

# Add clickable modifier to the Account card
card_pattern = r'(\.testTag\("account_card_\$\{account\.id\}"\)),\n                    shape = RoundedCornerShape\(16\.dp\)'
new_card_pattern = r'\1\n                        .clickable { selectedAccountForTransactions = account },\n                    shape = RoundedCornerShape(16.dp)'
content = re.sub(card_pattern, new_card_pattern, content)

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "w") as f:
    f.write(content)
