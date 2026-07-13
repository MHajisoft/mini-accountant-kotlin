with open("app/src/main/java/com/example/ui/FinancialApp.kt", "r") as f:
    lines = f.readlines()

out = []
for idx, line in enumerate(lines):
    if "DIALOG: ADD NEW TRANSACTION" in line:
        break
    out.append(line)

# Remove the line before it as well if it's a separator
if out[-1].strip() == "// ------------------------------------":
    out.pop()

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "w") as f:
    f.writelines(out)
