import com.example.data.JalaliCalendarHelper

fun main() {
    val jd = JalaliCalendarHelper.JalaliDate(1405, 4, 22)
    val gd = JalaliCalendarHelper.jalaliToGregorian(jd)
    println("Jalali 1405/4/22 -> Gregorian ${gd.year}/${gd.month}/${gd.day}")
    val jd2 = JalaliCalendarHelper.gregorianToJalali(gd)
    println("Gregorian ${gd.year}/${gd.month}/${gd.day} -> Jalali ${jd2.year}/${jd2.month}/${jd2.day}")
    
    val gd3 = JalaliCalendarHelper.GregorianDate(2026, 7, 12)
    val jd3 = JalaliCalendarHelper.gregorianToJalali(gd3)
    println("Gregorian 2026/7/12 -> Jalali ${jd3.year}/${jd3.month}/${jd3.day}")
    val gd4 = JalaliCalendarHelper.jalaliToGregorian(jd3)
    println("Jalali ${jd3.year}/${jd3.month}/${jd3.day} -> Gregorian ${gd4.year}/${gd4.month}/${gd4.day}")
}
