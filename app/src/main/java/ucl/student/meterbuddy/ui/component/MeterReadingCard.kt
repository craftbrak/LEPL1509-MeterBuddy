package ucl.student.meterbuddy.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import co.yml.charts.common.extensions.isNotNull
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun MeterReadingCard(
    modifier: Modifier,
    onclick : ()-> Unit,
    value: Float,
    date: LocalDateTime,
    note: String?,
    onDeleteClick: ()-> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    Row (modifier.clickable { onclick() }.clip(shape = RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.primaryContainer), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround){
        Text(text = value.toString())
        Text(text = date.format(formatter))
        if (note.isNotNull()) { Text(text = note.toString()) }
        else { Text(text = "No note") }
        IconButton(onClick = onDeleteClick) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "edit")
        }
    }
}