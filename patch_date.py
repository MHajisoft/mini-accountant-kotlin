import re

with open("app/src/main/java/com/example/ui/AddTransactionPage.kt", "r") as f:
    content = f.read()

content = content.replace('Text("$year")', 'Text(year.toString().formatByLang(lang))')
content = content.replace('Text("$month")', 'Text(month.toString().formatByLang(lang))')
content = content.replace('Text("$day")', 'Text(day.toString().formatByLang(lang))')

content = content.replace('"${jDate.year}/${jDate.month}/${jDate.day}"', '"${jDate.year}/${jDate.month}/${jDate.day}".formatByLang(if(isJalali) AppLanguage.FA else AppLanguage.EN)')
content = content.replace('"${cal.get(Calendar.YEAR)}/${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.DAY_OF_MONTH)}"', '"${cal.get(Calendar.YEAR)}/${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.DAY_OF_MONTH)}".formatByLang(if(isJalali) AppLanguage.FA else AppLanguage.EN)')


with open("app/src/main/java/com/example/ui/AddTransactionPage.kt", "w") as f:
    f.write(content)
