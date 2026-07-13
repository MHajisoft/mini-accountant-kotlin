import re

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "r") as f:
    content = f.read()

content = content.replace("fun formatMoney(amount: Double, lang: AppLanguage).formatByLang(activeLang): String", "fun formatMoney(amount: Double, lang: AppLanguage): String")

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/AccountTransactionsDialog.kt", "r") as f:
    content = f.read()
    
content = content.replace("fun formatMoney(amount: Double, lang: AppLanguage).formatByLang(activeLang): String", "fun formatMoney(amount: Double, lang: AppLanguage): String")

with open("app/src/main/java/com/example/ui/AccountTransactionsDialog.kt", "w") as f:
    f.write(content)
