package ui.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.colors.Colors

@Composable
fun WidgetBase(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Surface(modifier = modifier.padding(5.dp), color = Colors.foreground, shape = RoundedCornerShape(10), border = BorderStroke(2.dp, Colors.text)) {
        Box(Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
            content()
        }
    }
}