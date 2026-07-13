def gregorianToJalali(gYear, gMonth, gDay):
    gDaysInMonth = [0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334]
    gy = gYear - 1600
    gm = gMonth - 1
    gd = gDay - 1
    gDayNo = 365 * gy + (gy + 3) // 4 - (gy + 99) // 100 + (gy + 399) // 400
    gDayNo += gDaysInMonth[gm]
    if gm > 1 and ((gYear % 4 == 0 and gYear % 100 != 0) or (gYear % 400 == 0)):
        gDayNo += 1
    gDayNo += gd
    jDayNo = gDayNo - 79
    jNp = jDayNo // 12053
    jDayNo %= 12053
    jy = 979 + 33 * jNp + 4 * (jDayNo // 1461)
    jDayNo %= 1461
    if jDayNo >= 366:
        jy += (jDayNo - 1) // 365
        jDayNo = (jDayNo - 1) % 365
    jm = 0
    jd = 0
    for i in range(12):
        days = 31 if i < 6 else (30 if i < 11 else 29)
        if jDayNo < days:
            jm = i + 1
            jd = jDayNo + 1
            break
        jDayNo -= days
    return jy, jm, jd

def jalaliToGregorian(jYear, jMonth, jDay):
    jy = jYear - 979
    jm = jMonth - 1
    jd = jDay - 1
    jDayNo = 365 * jy + (jy // 33) * 8 + (jy % 33 + 3) // 4
    for i in range(jm):
        jDayNo += 31 if i < 6 else 30
    jDayNo += jd
    gDayNo = jDayNo + 79
    gy = 1600 + 400 * (gDayNo // 146097)
    gDayNo %= 146097
    leap = True
    if gDayNo >= 36525:
        gDayNo -= 1
        gy += 100 * (gDayNo // 36524)
        gDayNo %= 36524
        if gDayNo >= 365:
            gDayNo += 1
        else:
            leap = False
    gy += 4 * (gDayNo // 1461)
    gDayNo %= 1461
    if gDayNo >= 366:
        gy += (gDayNo - 1) // 365
        gDayNo = (gDayNo - 1) % 365
        leap = False
    gd = gDayNo
    gDaysInMonth = [31, 29 if leap else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]
    gm = 0
    for i in range(12):
        days = gDaysInMonth[i]
        if gd < days:
            gm = i + 1
            gd = gd + 1
            break
        gd -= days
    return gy, gm, gd

print("Jalali 1405/4/22 ->", jalaliToGregorian(1405, 4, 22))
print("Gregorian 2026/7/13 ->", gregorianToJalali(2026, 7, 13))

print("Jalali 1405/4/21 ->", jalaliToGregorian(1405, 4, 21))
print("Gregorian 2026/7/12 ->", gregorianToJalali(2026, 7, 12))
