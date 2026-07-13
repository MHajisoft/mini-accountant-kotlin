import re

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "r") as f:
    content = f.read()

# Add state variables for sheets
state_vars = """    var selectedCategory by remember { mutableStateOf(if (initialType == "EXPENSE") expenseCategories.firstOrNull() ?: "Other" else incomeCategories.firstOrNull() ?: "Other") }
    // Sync category when initial type changes"""

new_state_vars = """    var selectedCategory by remember { mutableStateOf(if (initialType == "EXPENSE") expenseCategories.firstOrNull() ?: "Other" else incomeCategories.firstOrNull() ?: "Other") }
    
    var showSourceAccountSheet by remember { mutableStateOf(false) }
    var showTargetAccountSheet by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }
    
    // Sync category when initial type changes"""

content = content.replace(state_vars, new_state_vars)

# Remove `var expandedSection by remember { mutableStateOf<String?>("SOURCE_ACC") }`
content = re.sub(r'\s*// Default expand the first section[^\n]*\n\s*var expandedSection by remember \{ mutableStateOf<String\?>\("SOURCE_ACC"\) \}', '', content)

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "w") as f:
    f.write(content)
