import re

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "r") as f:
    content = f.read()

# For balance
content = content.replace(
    'onValueChange = { balance = it },',
    'onValueChange = { balance = it.replace(Regex("[^\\\\d.]"), "") },'
)
content = content.replace(
    'keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),',
    'keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),\n                    visualTransformation = ThousandsSeparatorVisualTransformation(),'
)

# For amount
content = content.replace(
    'onValueChange = { amount = it },',
    'onValueChange = { amount = it.replace(Regex("[^\\\\d.]"), "") },'
)
content = content.replace(
    'keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),',
    'keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),\n                            visualTransformation = ThousandsSeparatorVisualTransformation(),'
)

with open("app/src/main/java/com/example/ui/FinancialApp.kt", "w") as f:
    f.write(content)
