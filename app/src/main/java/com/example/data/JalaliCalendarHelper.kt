package com.example.data

import java.util.Calendar

object JalaliCalendarHelper {
    class JalaliDate(val year: Int, val month: Int, val day: Int) {
        override fun toString(): String = "$year/${month.toString().padStart(2, '0')}/${day.toString().padStart(2, '0')}"
    }

    class GregorianDate(val year: Int, val month: Int, val day: Int) {
        override fun toString(): String = "$year/${month.toString().padStart(2, '0')}/${day.toString().padStart(2, '0')}"
    }

    val JALALI_MONTH_NAMES_FA = listOf(
        "فروردین", "اردیبهشت", "خرداد",
        "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر",
        "دی", "بهمن", "اسفند"
    )

    val GREGORIAN_MONTH_NAMES_EN = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    val GREGORIAN_MONTH_NAMES_AR = listOf(
        "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
        "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
    )

    val FA_WEEKDAYS = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")
    val EN_WEEKDAYS = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
    val AR_WEEKDAYS = listOf("أح", "ن", "ث", "ر", "خ", "ج", "س")

    fun gregorianToJalali(gregorian: GregorianDate): JalaliDate {
        val gYear = gregorian.year
        val gMonth = gregorian.month
        val gDay = gregorian.day

        val gDaysInMonth = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        var gy = gYear - 1600
        var gm = gMonth - 1
        val gd = gDay - 1

        var gDayNo = 365 * gy + (gy + 3) / 4 - (gy + 99) / 100 + (gy + 399) / 400
        gDayNo += gDaysInMonth[gm]
        if (gm > 1 && ((gYear % 4 == 0 && gYear % 100 != 0) || (gYear % 400 == 0))) {
            gDayNo++
        }
        gDayNo += gd

        var jDayNo = gDayNo - 79

        val jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        var jm = 0
        var jd = 0
        for (i in 0..11) {
            val days = if (i < 6) 31 else if (i < 11) 30 else 29
            if (jDayNo < days) {
                jm = i + 1
                jd = jDayNo + 1
                break
            }
            jDayNo -= days
        }

        return JalaliDate(jy, jm, jd)
    }

    fun jalaliToGregorian(jalali: JalaliDate): GregorianDate {
        val jYear = jalali.year
        val jMonth = jalali.month
        val jDay = jalali.day

        var jy = jYear - 979
        var jm = jMonth - 1
        val jd = jDay - 1

        var jDayNo = 365 * jy + (jy / 33) * 8 + (jy % 33 + 3) / 4
        for (i in 0 until jm) {
            jDayNo += if (i < 6) 31 else 30
        }
        jDayNo += jd

        var gDayNo = jDayNo + 79

        var gy = 1600 + 400 * (gDayNo / 146097)
        gDayNo %= 146097

        var leap = true
        if (gDayNo >= 36525) {
            gDayNo--
            gy += 100 * (gDayNo / 36524)
            gDayNo %= 36524
            if (gDayNo >= 365) {
                gDayNo++
            } else {
                leap = false
            }
        }

        gy += 4 * (gDayNo / 1461)
        gDayNo %= 1461

        if (gDayNo >= 366) {
            gy += (gDayNo - 1) / 365
            gDayNo = (gDayNo - 1) % 365
            leap = false
        }

        var gd = gDayNo

        val gDaysInMonth = intArrayOf(31, if (leap) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var gm = 0
        for (i in 0..11) {
            val days = gDaysInMonth[i]
            if (gd < days) {
                gm = i + 1
                gd = gd + 1
                break
            }
            gd -= days
        }

        return GregorianDate(gy, gm, gd)
    }

    fun getJalaliDaysInMonth(year: Int, month: Int): Int {
        if (month in 1..6) return 31
        if (month in 7..11) return 30
        if (month == 12) {
            val r = (year + 38) * 31 % 128
            return if (r < 31) 30 else 29
        }
        return 30
    }

    fun getJalaliFirstDayOfWeek(year: Int, month: Int): Int {
        val greg = jalaliToGregorian(JalaliDate(year, month, 1))
        val cal = Calendar.getInstance()
        cal.set(greg.year, greg.month - 1, greg.day)
        return cal.get(Calendar.DAY_OF_WEEK) // 1 = Sun, 2 = Mon, ..., 7 = Sat
    }

    fun getGregorianDaysInMonth(year: Int, month: Int): Int {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month - 1)
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun getGregorianFirstDayOfWeek(year: Int, month: Int): Int {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 1)
        return cal.get(Calendar.DAY_OF_WEEK)
    }
}
