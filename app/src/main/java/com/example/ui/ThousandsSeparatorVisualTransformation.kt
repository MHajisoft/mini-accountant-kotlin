package com.example.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat

class ThousandsSeparatorVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) return TransformedText(text, OffsetMapping.Identity)
        
        val digits = originalText.replace(Regex("[^\\d.]"), "")
        if (digits.isEmpty()) return TransformedText(text, OffsetMapping.Identity)
        
        // Handle decimal part separately if it exists
        val parts = digits.split(".")
        val intPart = parts[0]
        val decPart = if (parts.size > 1) "." + parts[1] else ""
        
        val formattedInt = try {
            val df = DecimalFormat("#,###")
            df.format(intPart.toLong())
        } catch (e: Exception) {
            intPart
        }
        
        val formatted = formattedInt + decPart
        
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (originalText.isEmpty()) return 0
                val textBeforeCursor = originalText.substring(0, offset.coerceAtMost(originalText.length))
                var digitCount = textBeforeCursor.count { it.isDigit() || it == '.' }
                
                var transformedOffset = 0
                var currDigitCount = 0
                for (i in formatted.indices) {
                    if (currDigitCount == digitCount) break
                    if (formatted[i].isDigit() || formatted[i] == '.') {
                        currDigitCount++
                    }
                    transformedOffset++
                }
                return transformedOffset
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (formatted.isEmpty()) return 0
                val textBeforeCursor = formatted.substring(0, offset.coerceAtMost(formatted.length))
                var digitCount = textBeforeCursor.count { it.isDigit() || it == '.' }
                
                var originalOffset = 0
                var currDigitCount = 0
                for (i in originalText.indices) {
                    if (currDigitCount == digitCount) break
                    if (originalText[i].isDigit() || originalText[i] == '.') {
                        currDigitCount++
                    }
                    originalOffset++
                }
                return originalOffset
            }
        }
        
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
