import re

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "r") as f:
    content = f.read()

pattern = r'\.forEach \{ acc ->[\s\S]*?\}// 4\. Category Selection'

new_content = re.sub(r'\}\n\s*\.forEach \{ acc ->[\s\S]*?\}// 4\. Category Selection', '} // 4. Category Selection', content)

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "w") as f:
    f.write(new_content)
