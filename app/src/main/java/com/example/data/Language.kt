package com.example.data

import androidx.compose.ui.unit.LayoutDirection

enum class AppLanguage(val code: String, val displayName: String, val layoutDirection: LayoutDirection) {
    EN("en", "English", LayoutDirection.Ltr),
    FA("fa", "فارسی (Persian)", LayoutDirection.Rtl),
    AR("ar", "العربية (Arabic)", LayoutDirection.Rtl)
}

object Translations {
    private val dictionary = mapOf(
        "app_title" to mapOf(
            AppLanguage.EN to "Finance Tracker",
            AppLanguage.FA to "دفترچه مالی",
            AppLanguage.AR to "تعقب المالية"
        ),
        "accounts" to mapOf(
            AppLanguage.EN to "Accounts",
            AppLanguage.FA to "حساب‌ها",
            AppLanguage.AR to "الحسابات"
        ),
        "transactions" to mapOf(
            AppLanguage.EN to "Transactions",
            AppLanguage.FA to "تراکنش‌ها",
            AppLanguage.AR to "المعاملات"
        ),
        "reports" to mapOf(
            AppLanguage.EN to "Reports",
            AppLanguage.FA to "گزارش‌ها",
            AppLanguage.AR to "التقارير"
        ),
        "settings" to mapOf(
            AppLanguage.EN to "Settings",
            AppLanguage.FA to "تنظیمات",
            AppLanguage.AR to "الإعدادات"
        ),
        "balance" to mapOf(
            AppLanguage.EN to "Balance",
            AppLanguage.FA to "موجودی",
            AppLanguage.AR to "الرصيد"
        ),
        "total_balance" to mapOf(
            AppLanguage.EN to "Total Balance",
            AppLanguage.FA to "کل موجودی",
            AppLanguage.AR to "إجمالي الرصيد"
        ),
        "add_account" to mapOf(
            AppLanguage.EN to "Add Account",
            AppLanguage.FA to "افزودن حساب",
            AppLanguage.AR to "إضافة حساب"
        ),
        "add_transaction" to mapOf(
            AppLanguage.EN to "Add Transaction",
            AppLanguage.FA to "افزودن تراکنش",
            AppLanguage.AR to "إضافة معاملة"
        ),
        "account_name" to mapOf(
            AppLanguage.EN to "Account Name",
            AppLanguage.FA to "نام حساب",
            AppLanguage.AR to "اسم الحساب"
        ),
        "initial_balance" to mapOf(
            AppLanguage.EN to "Initial Balance",
            AppLanguage.FA to "موجودی اولیه",
            AppLanguage.AR to "الرصيد الأولي"
        ),
        "select_account" to mapOf(
            AppLanguage.EN to "Select Account",
            AppLanguage.FA to "انتخاب حساب",
            AppLanguage.AR to "اختر الحساب"
        ),
        "from_account" to mapOf(
            AppLanguage.EN to "From Account",
            AppLanguage.FA to "از حساب",
            AppLanguage.AR to "من الحساب"
        ),
        "to_account" to mapOf(
            AppLanguage.EN to "To Account",
            AppLanguage.FA to "به حساب",
            AppLanguage.AR to "إلى الحساب"
        ),
        "amount" to mapOf(
            AppLanguage.EN to "Amount",
            AppLanguage.FA to "مبلغ",
            AppLanguage.AR to "المبلغ"
        ),
        "category" to mapOf(
            AppLanguage.EN to "Category",
            AppLanguage.FA to "دسته‌بندی",
            AppLanguage.AR to "الفئة"
        ),
        "description" to mapOf(
            AppLanguage.EN to "Description",
            AppLanguage.FA to "توضیحات",
            AppLanguage.AR to "الوصف"
        ),
        "date" to mapOf(
            AppLanguage.EN to "Date",
            AppLanguage.FA to "تاریخ",
            AppLanguage.AR to "التاريخ"
        ),
        "save" to mapOf(
            AppLanguage.EN to "Save",
            AppLanguage.FA to "ذخیره",
            AppLanguage.AR to "حفظ"
        ),
        "cancel" to mapOf(
            AppLanguage.EN to "Cancel",
            AppLanguage.FA to "انصراف",
            AppLanguage.AR to "إلغاء"
        ),
        "income" to mapOf(
            AppLanguage.EN to "Income",
            AppLanguage.FA to "درآمد",
            AppLanguage.AR to "الدخل"
        ),
        "expense" to mapOf(
            AppLanguage.EN to "Expense",
            AppLanguage.FA to "هزینه",
            AppLanguage.AR to "المصروف"
        ),
        "transfer" to mapOf(
            AppLanguage.EN to "Transfer",
            AppLanguage.FA to "انتقال",
            AppLanguage.AR to "تحويل"
        ),
        "recent_transactions" to mapOf(
            AppLanguage.EN to "Recent Transactions",
            AppLanguage.FA to "تراکنش‌های اخیر",
            AppLanguage.AR to "المعاملات الأخيرة"
        ),
        "no_transactions" to mapOf(
            AppLanguage.EN to "No transactions found",
            AppLanguage.FA to "تراکنش یافت نشد",
            AppLanguage.AR to "لم يتم العثور على معاملات"
        ),
        "no_accounts" to mapOf(
            AppLanguage.EN to "No accounts found. Please add an account.",
            AppLanguage.FA to "حسابی یافت نشد. لطفا یک حساب بسازید.",
            AppLanguage.AR to "لم يتم العثور على حسابات. يرجى إضافة حساب."
        ),
        "theme" to mapOf(
            AppLanguage.EN to "Theme",
            AppLanguage.FA to "پوسته",
            AppLanguage.AR to "السمة"
        ),
        "language" to mapOf(
            AppLanguage.EN to "Language",
            AppLanguage.FA to "زبان",
            AppLanguage.AR to "اللغة"
        ),
        "backup_restore" to mapOf(
            AppLanguage.EN to "Backup & Restore",
            AppLanguage.FA to "پشتیبان‌گیری و بازیابی",
            AppLanguage.AR to "النسخ الاحتياطي والاستعادة"
        ),
        "export_backup" to mapOf(
            AppLanguage.EN to "Export JSON Backup",
            AppLanguage.FA to "خروجی فایل پشتیبان (JSON)",
            AppLanguage.AR to "تصدير نسخة احتياطية (JSON)"
        ),
        "import_backup" to mapOf(
            AppLanguage.EN to "Import JSON Backup",
            AppLanguage.FA to "وارد کردن فایل پشتیبان (JSON)",
            AppLanguage.AR to "استيراد نسخة احتياطية (JSON)"
        ),
        "sync_cloud" to mapOf(
            AppLanguage.EN to "Sync with Cloud Storage",
            AppLanguage.FA to "همگام‌سازی با فضای ابری",
            AppLanguage.AR to "مزامنة مع السحابة"
        ),
        "sync_desc" to mapOf(
            AppLanguage.EN to "Google Drive / OneDrive seamless automated synchronization.",
            AppLanguage.FA to "همگام‌سازی خودکار و یکپارچه با گوگل درایو / وان درایو.",
            AppLanguage.AR to "مزامنة تلقائية وسلسة مع جوجل درايف / وان درايف."
        ),
        "sync_now" to mapOf(
            AppLanguage.EN to "Sync Now",
            AppLanguage.FA to "همگام‌سازی فوری",
            AppLanguage.AR to "مزامنة الآن"
        ),
        "synchronized" to mapOf(
            AppLanguage.EN to "Synchronized Successfully",
            AppLanguage.FA to "همگام‌سازی موفقیت‌آمیز بود",
            AppLanguage.AR to "تمت المزامنة بنجاح"
        ),
        "syncing" to mapOf(
            AppLanguage.EN to "Syncing with Cloud...",
            AppLanguage.FA to "در حال همگام‌سازی با ابر...",
            AppLanguage.AR to "جاري المزامنة مع السحابة..."
        ),
        "delete" to mapOf(
            AppLanguage.EN to "Delete",
            AppLanguage.FA to "حذف",
            AppLanguage.AR to "حذف"
        ),
        "delete_confirm" to mapOf(
            AppLanguage.EN to "Are you sure you want to delete this?",
            AppLanguage.FA to "آیا از حذف این مورد مطمئن هستید؟",
            AppLanguage.AR to "هل أنت متأكد أنك تريد حذف هذا؟"
        ),
        "income_vs_expense" to mapOf(
            AppLanguage.EN to "Income vs Expense",
            AppLanguage.FA to "درآمد در مقابل هزینه",
            AppLanguage.AR to "الدخل مقابل المصروف"
        ),
        "expenses_by_category" to mapOf(
            AppLanguage.EN to "Expenses by Category",
            AppLanguage.FA to "هزینه‌ها بر اساس دسته‌بندی",
            AppLanguage.AR to "المصروفات حسب الفئة"
        ),
        "select_category" to mapOf(
            AppLanguage.EN to "Select Category",
            AppLanguage.FA to "انتخاب دسته‌بندی",
            AppLanguage.AR to "اختر الفئة"
        ),
        "add_account_fab" to mapOf(
            AppLanguage.EN to "New Account",
            AppLanguage.FA to "حساب جدید",
            AppLanguage.AR to "حساب جديد"
        ),
        "add_tx_fab" to mapOf(
            AppLanguage.EN to "New Transaction",
            AppLanguage.FA to "تراکنش جدید",
            AppLanguage.AR to "معاملة جديدة"
        )
    )

    fun getString(key: String, language: AppLanguage): String {
        return dictionary[key]?.get(language) ?: key
    }
}


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
