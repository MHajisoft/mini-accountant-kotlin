import re

with open("app/src/main/java/com/example/ui/AddTransactionPage.kt", "r") as f:
    content = f.read()

topbar_pattern = r'        topBar = \{\n\s*Surface\(shadowElevation = 4\.dp, color = MaterialTheme\.colorScheme\.surface\) \{\n\s*Row\(\n\s*modifier = Modifier\.fillMaxWidth\(\)\.padding\(horizontal = 8\.dp, vertical = 12\.dp\),\n\s*verticalAlignment = Alignment\.CenterVertically\n\s*\) \{\n\s*IconButton\(onClick = onDismiss\) \{\n\s*Icon\(Icons\.Default\.ArrowBack, contentDescription = "Back"\)\n\s*\}\n\s*Text\(\n\s*text = Translations\.getString\("add_transaction", activeLang\),\n\s*style = MaterialTheme\.typography\.titleLarge,\n\s*fontWeight = FontWeight\.Bold,\n\s*color = MaterialTheme\.colorScheme\.primary,\n\s*modifier = Modifier\.weight\(1f\)\.padding\(start = 8\.dp\)\n\s*\)\n\s*\}\n\s*\}\n\s*\}'

new_top_bottom_bars = """        topBar = {
            Surface(shadowElevation = 4.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = Translations.getString("add_transaction", activeLang),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .navigationBarsPadding(),
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
            }
        }"""

content = re.sub(topbar_pattern, new_top_bottom_bars, content)

# 2. Remove Save/Cancel buttons at the bottom of the LazyColumn
bottom_buttons_pattern = r'            item \{\n\s*Spacer\(modifier = Modifier\.height\(24\.dp\)\)\n\s*Row\(\n\s*modifier = Modifier\.fillMaxWidth\(\)\.padding\(bottom = 32\.dp\),\n\s*horizontalArrangement = Arrangement\.End,\n\s*verticalAlignment = Alignment\.CenterVertically\n\s*\) \{\n\s*TextButton\(onClick = onDismiss\) \{\n\s*Text\(Translations\.getString\("cancel", activeLang\), fontSize = 16\.sp\)\n\s*\}\n\s*Spacer\(modifier = Modifier\.width\(16\.dp\)\)\n\s*Button\(\n\s*onClick = \{\n\s*val amt = amount\.toDoubleOrNull\(\) \?: 0\.0\n\s*if \(amt > 0\) \{\n\s*onSave\(amt, currentType, selectedCategory, selectedAccountId, if \(currentType == "TRANSFER"\) selectedTargetAccountId else null, selectedTimestamp, description\)\n\s*\}\n\s*\}\n\s*\) \{\n\s*Text\(Translations\.getString\("save", activeLang\), fontSize = 16\.sp\)\n\s*\}\n\s*\}\n\s*\}'
content = re.sub(bottom_buttons_pattern, '', content)

with open("app/src/main/java/com/example/ui/AddTransactionPage.kt", "w") as f:
    f.write(content)
