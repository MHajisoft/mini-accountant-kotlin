import re

with open("app/src/main/java/com/example/ui/AddTransactionPage.kt", "r") as f:
    content = f.read()

pattern = r'    var year by remember \{ mutableStateOf\(Calendar\.getInstance\(\)\.apply \{ timeInMillis = initialTimestamp \}\.get\(Calendar\.YEAR\)\) \}\n\s*var month by remember \{ mutableStateOf\(Calendar\.getInstance\(\)\.apply \{ timeInMillis = initialTimestamp \}\.get\(Calendar\.MONTH\) \+ 1\) \}\n\s*var day by remember \{ mutableStateOf\(Calendar\.getInstance\(\)\.apply \{ timeInMillis = initialTimestamp \}\.get\(Calendar\.DAY_OF_MONTH\)\) \}\n\n\s*if \(isJalali\) \{\n\s*val initialJDate = JalaliCalendarHelper\.gregorianToJalali\(JalaliCalendarHelper\.GregorianDate\(year, month, day\)\)\n\s*year = initialJDate\.year\n\s*month = initialJDate\.month\n\s*day = initialJDate\.day\n\s*\}'

replacement = """    val initialDate = remember(initialTimestamp) {
        val cal = Calendar.getInstance().apply { timeInMillis = initialTimestamp }
        if (isJalali) {
            val j = JalaliCalendarHelper.gregorianToJalali(JalaliCalendarHelper.GregorianDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)))
            Triple(j.year, j.month, j.day)
        } else {
            Triple(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
        }
    }
    
    var year by remember { mutableStateOf(initialDate.first) }
    var month by remember { mutableStateOf(initialDate.second) }
    var day by remember { mutableStateOf(initialDate.third) }"""

content = re.sub(pattern, replacement, content)

with open("app/src/main/java/com/example/ui/AddTransactionPage.kt", "w") as f:
    f.write(content)
