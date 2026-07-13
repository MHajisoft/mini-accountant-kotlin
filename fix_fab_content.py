import re

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "r") as f:
    content = f.read()

old_fab = """                        FloatingActionButton(
                            onClick = {
                                preselectedTransactionType = "EXPENSE"
                                showAddTransactionPage = true
                            },
                            icon = { Icon(Icons.Default.Add, contentDescription = null) },
                            
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.testTag("fab_add_transaction")
                        )"""

new_fab = """                        FloatingActionButton(
                            onClick = {
                                preselectedTransactionType = "EXPENSE"
                                showAddTransactionPage = true
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.testTag("fab_add_transaction")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                        }"""

content = content.replace(old_fab, new_fab)

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "w") as f:
    f.write(content)
