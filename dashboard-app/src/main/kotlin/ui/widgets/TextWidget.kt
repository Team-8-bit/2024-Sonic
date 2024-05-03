package ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import ui.colors.Colors

/** Text widget. */
@Composable
fun TextWidget(name: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = name, color = Colors.text, fontSize = 20.sp, textAlign = TextAlign.Center)
        Text(text = value, color = Colors.text, fontSize = 15.sp, fontStyle = FontStyle.Italic, textAlign = TextAlign.Center)
    }
}