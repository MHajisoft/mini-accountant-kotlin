import re

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "r") as f:
    content = f.read()

delete_pattern = r'                            if \(\!confirmDelete\) \{.*?\n                            \} else \{\n                                Row\(verticalAlignment = Alignment\.CenterVertically\) \{\n                                    TextButton\(onClick = \{ confirmDelete = false \}\) \{\n                                        Text\(Translations\.getString\("cancel", activeLang\), fontSize = 12\.sp\)\n                                    \}\n                                    IconButton\(\n                                        onClick = \{\n                                            viewModel\.deleteAccount\(account\)\n                                            confirmDelete = false\n                                        \}\n                                    \) \{\n                                        Icon\(\n                                            Icons\.Default\.Check,\n                                            contentDescription = "Confirm",\n                                            tint = MaterialTheme\.colorScheme\.error\n                                        \)\n                                    \}\n                                \}\n                            \}'

content = re.sub(delete_pattern, '', content, flags=re.DOTALL)
content = content.replace('var confirmDelete by remember { mutableStateOf(false) }\n', '')

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "w") as f:
    f.write(content)
