package ucl.student.meterbuddy.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import co.yml.charts.common.extensions.isNotNull
import ucl.student.meterbuddy.data.model.entity.Housing

@Composable
fun HousingDialog(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onSubmit: (Housing)->Unit,
    onDismissRequest: ()->Unit,
    initialData: Housing? = null
) {
    if (enabled) {
        Dialog(onDismissRequest = onDismissRequest) {
            Card(modifier) {
                Column(modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (initialData.isNotNull()) {
                            "Edit your Home"
                        } else "Add a Home",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    HousingFrom(modifier=Modifier.fillMaxSize(),onSubmit = { housing -> onSubmit(housing) })
                }

            }
        }
    }
}

@Preview(name = "HousingDialog")
@Composable
private fun PreviewHousingDialog() {
    HousingDialog(enabled = true, onDismissRequest =  {}, onSubmit = {})
}