import re

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "r") as f:
    content = f.read()

# Pattern for Vault Button column
vault_pattern = r'\s*// Vault Button\n\s*Column\(\n\s*horizontalAlignment = Alignment\.CenterHorizontally,\n\s*modifier = Modifier\.weight\(1f\)\n\s*\) \{\n\s*Box\(\n\s*modifier = Modifier\n\s*\.size\(56\.dp\)\n\s*\.clip\(RoundedCornerShape\(16\.dp\)\)\n\s*\.background\(Color\(0xFFF3E5F5\)\) // purple-100\n\s*\.clickable \{\n\s*onQuickActionClick\("VAULT"\)\n\s*\},\n\s*contentAlignment = Alignment\.Center\n\s*\) \{\n\s*Text\("▦", fontSize = 24\.sp, fontWeight = FontWeight\.Bold, color = Color\(0xFF6A1B9A\)\)\n\s*\}\n\s*Spacer\(modifier = Modifier\.height\(4\.dp\)\)\n\s*Text\(\n\s*text = "Vault",\n\s*fontSize = 11\.sp,\n\s*fontWeight = FontWeight\.Bold,\n\s*color = MaterialTheme\.colorScheme\.onBackground\.copy\(alpha = 0\.6f\)\n\s*\)\n\s*\}'

content = re.sub(vault_pattern, '', content)

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "w") as f:
    f.write(content)
