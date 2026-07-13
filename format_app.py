import re

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "r") as f:
    content = f.read()

if "import com.example.data.formatByLang" not in content:
    content = content.replace("import com.example.data.AppLanguage", "import com.example.data.AppLanguage\nimport com.example.data.formatByLang")

# Format formatMoney usages
content = re.sub(r'formatMoney\((.*?)\)', r'formatMoney(\1).formatByLang(activeLang)', content)

# Format the dates in transaction list
content = content.replace('Text(text = formattedDate,', 'Text(text = formattedDate.formatByLang(activeLang),')

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/AccountTransactionsDialog.kt", "r") as f:
    content = f.read()

if "import com.example.data.formatByLang" not in content:
    content = content.replace("import com.example.data.AppLanguage", "import com.example.data.AppLanguage\nimport com.example.data.formatByLang")

content = re.sub(r'formatMoney\((.*?)\)', r'formatMoney(\1).formatByLang(activeLang)', content)
content = content.replace('Text(text = formattedDate,', 'Text(text = formattedDate.formatByLang(activeLang),')

with open("app/src/main/java/com/example/ui/AccountTransactionsDialog.kt", "w") as f:
    f.write(content)

