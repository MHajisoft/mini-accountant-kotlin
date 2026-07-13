import re

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "r") as f:
    content = f.read()

content = content.replace("fun formatDate(timestamp: Long, lang: AppLanguage, activeLang): String", "fun formatDate(timestamp: Long, lang: AppLanguage): String")

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "w") as f:
    f.write(content)
