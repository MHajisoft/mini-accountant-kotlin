import re

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "r") as f:
    content = f.read()

pattern = r'fun formatDate\(timestamp: Long\): String \{\n\s*val sdf = SimpleDateFormat\("yyyy/MM/dd HH:mm", Locale\.getDefault\(\)\)\n\s*return sdf\.format\(Date\(timestamp\)\)\n\}'
replacement = """fun formatDate(timestamp: Long, lang: AppLanguage): String {
    val cal = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }
    val timeStr = String.format("%02d:%02d", cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE))
    val isJalali = lang == AppLanguage.FA
    val dateStr = if (isJalali) {
        val jDate = com.example.data.JalaliCalendarHelper.gregorianToJalali(com.example.data.JalaliCalendarHelper.GregorianDate(cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH) + 1, cal.get(java.util.Calendar.DAY_OF_MONTH)))
        "${jDate.year}/${jDate.month}/${jDate.day}"
    } else {
        "${cal.get(java.util.Calendar.YEAR)}/${cal.get(java.util.Calendar.MONTH) + 1}/${cal.get(java.util.Calendar.DAY_OF_MONTH)}"
    }
    return "$dateStr $timeStr".formatByLang(lang)
}"""
content = re.sub(pattern, replacement, content)

# update all formatDate calls
content = re.sub(r'formatDate\((.*?)\)', r'formatDate(\1, activeLang)', content)
# We might have `formatDate(tx.timestamp)`

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/AccountTransactionsDialog.kt", "r") as f:
    content = f.read()
content = re.sub(r'formatDate\((.*?)\)', r'formatDate(\1, activeLang)', content)

with open("app/src/main/java/com/example/ui/AccountTransactionsDialog.kt", "w") as f:
    f.write(content)
