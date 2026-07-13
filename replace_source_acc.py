import re

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "r") as f:
    content = f.read()

source_acc_pattern = r'            // 2\. Source Account Accordion Selection\n\s*item \{\n\s*val isExpanded = expandedSection == "SOURCE_ACC"[\s\S]*?Icon\(\n\s*imageVector = if \(isExpanded\) Icons\.Default\.ArrowDropUp else Icons\.Default\.ArrowDropDown,\n\s*contentDescription = null,\n\s*tint = MaterialTheme\.colorScheme\.primary\n\s*\)\n\s*\}\n\s*// Expanded list\n\s*AnimatedVisibility\(visible = isExpanded\) \{[\s\S]*?\}'

new_source_acc = """            // 2. Source Account Selection
            item {
                val selectedAcc = accounts.find { it.id == selectedAccountId } ?: accounts.firstOrNull()
                val accColor = try {
                    Color(android.graphics.Color.parseColor(selectedAcc?.colorHex ?: "#1a73e8"))
                } catch (e: Exception) {
                    MaterialTheme.colorScheme.primary
                }
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSourceAccountSheet = true }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(accColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = accColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = Translations.getString(if (currentType == "TRANSFER") "from_account" else "select_account", activeLang),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = selectedAcc?.name ?: "Select",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }"""
            
content = re.sub(source_acc_pattern, new_source_acc, content)

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "w") as f:
    f.write(content)
