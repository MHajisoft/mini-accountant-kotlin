import re

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "r") as f:
    content = f.read()

content = content.replace('text = "$percent%  ",', 'text = "$percent%  ".formatByLang(activeLang),')

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "w") as f:
    f.write(content)
