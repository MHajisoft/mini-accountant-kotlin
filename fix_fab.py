import re

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "r") as f:
    content = f.read()

pattern = r'ExtendedFloatingActionButton\([\s\S]*?text\s*=\s*\{[^}]*\},\s*containerColor'
replacement = r'FloatingActionButton(\n                            onClick = {\n                                preselectedTransactionType = "EXPENSE"\n                                showAddTransactionPage = true\n                            },\n                            containerColor'
# wait, safer way is to just do a direct replacement of "ExtendedFloatingActionButton(" to "FloatingActionButton(" and remove the "text = { ... },"

content = re.sub(r'ExtendedFloatingActionButton\(', 'FloatingActionButton(', content)
content = re.sub(r'text\s*=\s*\{\s*Text\(Translations\.getString\("add_transaction", activeLang\)\)\s*\},', '', content)

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "w") as f:
    f.write(content)
