package ucl.student.meterbuddy.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Remove
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CounterField(
    modifier: Modifier = Modifier,
    counterValue : String,
    onMinusChange: () ->Unit,
    onAddChange: () ->Unit,
) {

    Row(modifier.clip(RoundedCornerShape(64.dp))
        .background(MaterialTheme.colorScheme.secondaryContainer)
        .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        FilledTonalIconButton(onClick = {
            onMinusChange()
            },
            colors = IconButtonColors(MaterialTheme.colorScheme.primaryContainer,MaterialTheme.colorScheme.onPrimaryContainer,MaterialTheme.colorScheme.surfaceDim,MaterialTheme.colorScheme.onError)
        ) {
            Icon(imageVector = Icons.TwoTone.Remove, contentDescription = Icons.TwoTone.Remove.name )
        }
        Text(text = counterValue, style = MaterialTheme.typography.headlineMedium)
        FilledTonalIconButton(onClick = { onAddChange() },
            colors = IconButtonColors(MaterialTheme.colorScheme.primaryContainer,MaterialTheme.colorScheme.onPrimaryContainer,MaterialTheme.colorScheme.surfaceDim,MaterialTheme.colorScheme.onError)) {
            Icon(imageVector = Icons.TwoTone.Add, contentDescription = Icons.TwoTone.Add.name )
        }
    }
}

@Preview(name = "CounterField")
@Composable
private fun PreviewCounterField() {
    Surface {
        CounterField(modifier = Modifier.width(150.dp),counterValue = 0.toString(),{},{})
    }

}