package com.emrecan.havadurumu.ui.theme
import androidx.compose.material.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.emrecan.havadurumu.R

val CustomFont = FontFamily(
    Font(R.font.futura_normal),
    Font(R.font.futura_bold, FontWeight.Bold),
    Font(R.font.futura_medium, FontWeight.Medium)
)

// Set of Material typography styles to start with
val Typography = Typography(defaultFontFamily = CustomFont)