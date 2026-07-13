with open("app/src/main/java/com/example/ui/FinancialApp.kt", "r") as f:
    lines = f.readlines()

out = []
skip = False
for line in lines:
    if "fun AddTransactionTypeDialog(" in line:
        skip = True
    elif skip and "@OptIn(ExperimentalMaterial3Api::class)" in line:
        skip = False
        out.append(line)
    elif not skip:
        out.append(line)

# Since we already deleted `fun AddTransactionTypeDialog` line itself and its first part, the current file is missing the `fun AddTransactionTypeDialog` declaration!
# Let me see where I am currently.
