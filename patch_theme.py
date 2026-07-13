import re

with open("app/src/main/java/com/example/ui/theme/Theme.kt", "r") as f:
    content = f.read()

pattern = r'import androidx\.compose\.runtime\.Composable\nimport androidx\.compose\.ui\.graphics\.Color'
replacement = """import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.ui.AppLanguage"""
content = re.sub(pattern, replacement, content)

pattern2 = r'fun FinancialAppTheme\(\n\s*theme: AppTheme = AppTheme\.VIBRANT,\n\s*content: @Composable \(\) -> Unit\n\s*\) \{'
replacement2 = """fun FinancialAppTheme(
    theme: AppTheme = AppTheme.VIBRANT,
    lang: AppLanguage = AppLanguage.EN,
    content: @Composable () -> Unit
) {"""
content = re.sub(pattern2, replacement2, content)

pattern3 = r'    MaterialTheme\(\n\s*colorScheme = colorScheme,\n\s*typography = Typography,\n\s*content = content\n\s*\)'
replacement3 = """    val typography = getAppTypography(lang)
    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )"""
content = re.sub(pattern3, replacement3, content)

with open("app/src/main/java/com/example/ui/theme/Theme.kt", "w") as f:
    f.write(content)
