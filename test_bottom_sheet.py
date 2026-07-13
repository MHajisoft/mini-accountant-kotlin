import re

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "r") as f:
    content = f.read()

has_opt_in = "ExperimentalMaterial3Api" in content

if not has_opt_in:
    print("No OptIn found")
else:
    print("OptIn exists")
