import re

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "r") as f:
    content = f.read()

vault_action = r'                                if \(actionType == "VAULT"\) \{\n                                    selectedTab = 3 // Go to Settings tab\n                                \} else \{\n                                    preselectedTransactionType = actionType\n                                    showAddTransactionPage = true\n                                \}'
replacement = r'                                preselectedTransactionType = actionType\n                                showAddTransactionPage = true'

content = re.sub(vault_action, replacement, content)

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "w") as f:
    f.write(content)
