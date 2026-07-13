import re

with open("app/src/main/java/com/example/data/Language.kt", "r") as f:
    content = f.read()

if "formatByLang" not in content:
    content += """

fun String.formatByLang(lang: AppLanguage): String {
    if (lang == AppLanguage.EN) return this
    var result = this
    val en = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
    val fa = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
    val ar = arrayOf("٠", "١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩")
    val replacements = if (lang == AppLanguage.FA) fa else ar
    for (i in en.indices) {
        result = result.replace(en[i], replacements[i])
    }
    return result
}
"""
    with open("app/src/main/java/com/example/data/Language.kt", "w") as f:
        f.write(content)
