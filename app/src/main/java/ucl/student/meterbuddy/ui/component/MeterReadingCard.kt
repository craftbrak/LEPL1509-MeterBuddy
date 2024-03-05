package ucl.student.meterbuddy.ui.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MeterReadingCard(modifier: Modifier,onclick : ()-> Unit, value: Float, date: LocalDateTime, note: String , onEditClick: ()-> Unit ) {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    //Should be a card and need to be aligned
    Row (modifier.clickable { onclick() }.clip(shape = RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.primaryContainer), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround){
        Text(text = value.toString())
        Text(text = date.format(formatter))
        Text(text = note)
        IconButton(onClick = onEditClick) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "edit")
        }
    }
}