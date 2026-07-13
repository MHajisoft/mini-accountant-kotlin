import re

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "r") as f:
    content = f.read()

# Replace definition
old_def = """fun formatMoney(amount: Double): String {
    val df = DecimalFormat("#,##0.00")
    return "$" + df.format(amount)
}"""

new_def = """fun formatMoney(amount: Double, lang: AppLanguage): String {
    return if (lang == AppLanguage.FA) {
        val df = DecimalFormat("#,###")
        if (amount % 10.0 == 0.0) {
            df.format(amount / 10.0) + " تومان"
        } else {
            df.format(amount) + " ریال"
        }
    } else if (lang == AppLanguage.AR) {
        val df = DecimalFormat("#,##0.00")
        df.format(amount) + " د.إ"
    } else {
        val df = DecimalFormat("#,##0.00")
        "$" + df.format(amount)
    }
}"""

content = content.replace(old_def, new_def)

# Find all occurrences of formatMoney(something) and replace with formatMoney(something, activeLang) or something else.
# Wait, let's just do a regex replace.
content = re.sub(r'formatMoney\(([^,]+?)\)', r'formatMoney(\1, activeLang)', content)

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "w") as f:
    f.write(content)
