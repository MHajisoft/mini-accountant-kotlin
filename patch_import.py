import re

with open("app/src/main/java/com/example/ui/AddTransactionPage.kt", "r") as f:
    content = f.read()

if "import com.example.data.formatByLang" not in content:
    content = content.replace("import com.example.data.AppLanguage", "import com.example.data.AppLanguage\nimport com.example.data.formatByLang")

with open("app/src/main/java/com/example/ui/AddTransactionPage.kt", "w") as f:
    f.write(content)
