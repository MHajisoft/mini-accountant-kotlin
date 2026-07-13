import re

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

pattern = r'            val activeTheme by viewModel\.theme\.collectAsState\(\)\n\s*FinancialAppTheme\(theme = activeTheme\) \{'
replacement = """            val activeTheme by viewModel.theme.collectAsState()
            val activeLang by viewModel.language.collectAsState()
            FinancialAppTheme(theme = activeTheme, lang = activeLang) {"""

content = re.sub(pattern, replacement, content)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)
