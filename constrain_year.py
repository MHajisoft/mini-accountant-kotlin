import re

with open("app/src/main/java/com/example/ui/AddTransactionPage.kt", "r") as f:
    content = f.read()

content = content.replace("IconButton(onClick = { year++ })", "IconButton(onClick = { if (year < 2100) year++ })")
content = content.replace("IconButton(onClick = { year-- })", "IconButton(onClick = { if (year > 1300) year-- })")

with open("app/src/main/java/com/example/ui/AddTransactionPage.kt", "w") as f:
    f.write(content)
