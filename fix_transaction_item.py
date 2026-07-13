import re

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "r") as f:
    content = f.read()

pattern = r'(fun TransactionItem\([^)]+\)\s*\{\s*)(val categoryBgColor = when \{)'
new_text = r'\1var confirmDelete by remember { mutableStateOf(false) }\n    \2'

content = re.sub(pattern, new_text, content)

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "w") as f:
    f.write(content)
