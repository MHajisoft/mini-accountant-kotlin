import re

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "r") as f:
    content = f.read()

content = content.replace(".formatByLang(activeLang)", "")

pattern = r'fun formatMoney\(amount: Double, lang: AppLanguage\): String \{\n\s*return if \(lang == AppLanguage\.FA\) \{\n\s*val df = DecimalFormat\("#,###"\)\n\s*if \(amount % 10\.0 == 0\.0\) \{\n\s*df\.format\(amount / 10\.0\) \+ " تومان"\n\s*\} else \{\n\s*df\.format\(amount\) \+ " ریال"\n\s*\}\n\s*\} else if \(lang == AppLanguage\.AR\) \{\n\s*val df = DecimalFormat\("#,###\.##"\)\n\s*df\.format\(amount\) \+ " د\.إ"\n\s*\} else \{\n\s*val df = DecimalFormat\("\$#,###\.##"\)\n\s*df\.format\(amount\)\n\s*\}\n\}'
replacement = """fun formatMoney(amount: Double, lang: AppLanguage): String {
    val result = if (lang == AppLanguage.FA) {
        val df = DecimalFormat("#,###")
        if (amount % 10.0 == 0.0) {
            df.format(amount / 10.0) + " تومان"
        } else {
            df.format(amount) + " ریال"
        }
    } else if (lang == AppLanguage.AR) {
        val df = DecimalFormat("#,###.##")
        df.format(amount) + " د.إ"
    } else {
        val df = DecimalFormat("$#,###.##")
        df.format(amount)
    }
    return result.formatByLang(lang)
}"""
content = re.sub(pattern, replacement, content)
with open("app/src/main/java/com/example/ui/FinancialApp.kt", "w") as f:
    f.write(content)


with open("app/src/main/java/com/example/ui/AccountTransactionsDialog.kt", "r") as f:
    content = f.read()

content = content.replace(".formatByLang(activeLang)", "")
with open("app/src/main/java/com/example/ui/AccountTransactionsDialog.kt", "w") as f:
    f.write(content)

