package org.team9432.dashboard.app.ui.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.team9432.dashboard.app.io.Client
import org.team9432.dashboard.shared.BooleanWidget

/** Boolean displayed as a toggleable switch. */
@Composable
fun MutableBooleanWidget(name: String, value: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = name, fontSize = 20.sp, textAlign = TextAlign.Center)
        Switch(checked = value, onCheckedChange = { isChecked ->
            Client.updateWidget(BooleanWidget(name, isChecked, true))
        })
    }
}

/** Boolean displayed as a colored box. */
@Composable
fun ImmutableBooleanWidget(name: String, value: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = name, fontSize = 20.sp, textAlign = TextAlign.Center)
        Surface(
            Modifier.fillMaxWidth(0.6F).fillMaxHeight(0.2F).padding(top = 10.dp),
            color = if (value) Color.Green else Color.Red,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface)
        ) {}
    }
}