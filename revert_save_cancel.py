import re

with open("app/src/main/java/com/example/ui/AddTransactionPage.kt", "r") as f:
    content = f.read()

# 1. Remove the Save button from the TopBar
topbar_pattern = r'                    Button\(\n\s*onClick = \{\n\s*val amt = amount\.toDoubleOrNull\(\) \?: 0\.0\n\s*if \(amt > 0\) \{\n\s*onSave\(amt, currentType, selectedCategory, selectedAccountId, if \(currentType == "TRANSFER"\) selectedTargetAccountId else null, selectedTimestamp, description\)\n\s*\}\n\s*\},\n\s*colors = ButtonDefaults\.buttonColors\(containerColor = MaterialTheme\.colorScheme\.primary\)\n\s*\) \{\n\s*Text\(Translations\.getString\("save", activeLang\)\)\n\s*\}'
content = re.sub(topbar_pattern, '', content)

# 2. Add Save/Cancel buttons at the bottom of the LazyColumn
description_pattern = r'            item \{\n\s*OutlinedTextField\(\n\s*value = description,\n\s*onValueChange = \{ description = it \},\n\s*label = \{ Text\(Translations\.getString\("description", activeLang\)\) \},\n\s*singleLine = true,\n\s*shape = RoundedCornerShape\(16\.dp\),\n\s*modifier = Modifier\.fillMaxWidth\(\)\.testTag\("transaction_desc_input"\)\n\s*\)\n\s*\}'

save_cancel_buttons = """            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(Translations.getString("description", activeLang)) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().testTag("transaction_desc_input")
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(Translations.getString("cancel", activeLang), fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            val amt = amount.toDoubleOrNull() ?: 0.0
                            if (amt > 0) {
                                onSave(amt, currentType, selectedCategory, selectedAccountId, if (currentType == "TRANSFER") selectedTargetAccountId else null, selectedTimestamp, description)
                            }
                        }
                    ) {
                        Text(Translations.getString("save", activeLang), fontSize = 16.sp)
                    }
                }
            }"""

content = re.sub(description_pattern, save_cancel_buttons, content)

with open("app/src/main/java/com/example/ui/AddTransactionPage.kt", "w") as f:
    f.write(content)
