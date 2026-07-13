with open("app/src/main/java/com/example/ui/FinancialApp.kt", "r") as f:
    lines = f.readlines()

out = []
skip = False
for line in lines:
    if "}.forEach { acc ->" in line:
        skip = True
    elif skip and "// 4. Category Selection" in line:
        skip = False
        out.append(line)
    elif skip and "            // 4. Category Selection" in line:
        skip = False
        out.append(line)
    elif not skip:
        out.append(line)

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "w") as f:
    f.writelines(out)
