package ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** The base of all displayed widgets, adds background and border. */
@Composable
fun WidgetBase(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Card(modifier = modifier.padding(5.dp), shape = RoundedCornerShape(10),
    ) {
        Box(Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
            content()
        }
    }
}