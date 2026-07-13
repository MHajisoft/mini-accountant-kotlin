import re

with open("app/src/main/java/com/example/ui/AddTransactionPage.kt", "r") as f:
    content = f.read()

pattern = r'IconButton\(onClick = \{ month = if\(month == 12\) 1 else month \+ 1 \}\)'
replacement = """IconButton(onClick = { 
                            month = if(month == 12) 1 else month + 1 
                            val maxDays = if(isJalali) JalaliCalendarHelper.getJalaliDaysInMonth(year, month) else JalaliCalendarHelper.getGregorianDaysInMonth(year, month)
                            if (day > maxDays) day = maxDays
                        })"""
content = re.sub(pattern, replacement, content)

pattern = r'IconButton\(onClick = \{ month = if\(month == 1\) 12 else month - 1 \}\)'
replacement = """IconButton(onClick = { 
                            month = if(month == 1) 12 else month - 1 
                            val maxDays = if(isJalali) JalaliCalendarHelper.getJalaliDaysInMonth(year, month) else JalaliCalendarHelper.getGregorianDaysInMonth(year, month)
                            if (day > maxDays) day = maxDays
                        })"""
content = re.sub(pattern, replacement, content)

pattern = r'IconButton\(onClick = \{ day = if\(day == 31\) 1 else day \+ 1 \}\)'
replacement = """IconButton(onClick = { 
                            val maxDays = if(isJalali) JalaliCalendarHelper.getJalaliDaysInMonth(year, month) else JalaliCalendarHelper.getGregorianDaysInMonth(year, month)
                            day = if(day >= maxDays) 1 else day + 1 
                        })"""
content = re.sub(pattern, replacement, content)

pattern = r'IconButton\(onClick = \{ day = if\(day == 1\) 31 else day - 1 \}\)'
replacement = """IconButton(onClick = { 
                            val maxDays = if(isJalali) JalaliCalendarHelper.getJalaliDaysInMonth(year, month) else JalaliCalendarHelper.getGregorianDaysInMonth(year, month)
                            day = if(day <= 1) maxDays else day - 1 
                        })"""
content = re.sub(pattern, replacement, content)

with open("app/src/main/java/com/example/ui/AddTransactionPage.kt", "w") as f:
    f.write(content)
