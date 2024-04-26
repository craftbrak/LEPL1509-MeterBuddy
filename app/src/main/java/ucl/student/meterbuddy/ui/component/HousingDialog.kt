package ucl.student.meterbuddy.ui.component

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import ucl.student.meterbuddy.data.model.entity.User

@Composable
fun HousingDialog(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onSubmit: (Housing)->Unit,
    onDismissRequest: ()->Unit,
    initialData: Housing? = null,
    users: List<User> = emptyList(),
    usersOfHousing: List<User> = emptyList(),
    onUserDelete: (User) -> Unit,
    onUserAdd: (User) -> Unit
) {
    if (enabled) {
        Dialog(onDismissRequest = onDismissRequest) {
            Card(modifier) {
                Column(
                    modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (initialData.isNotNull()) {
                            "Edit your Home"
                        } else "Add a Home",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Log.wtf("HousingDialog", initialData?.housingName)
                    HousingFrom(modifier=Modifier.fillMaxWidth(),onSubmit = { housing -> onSubmit(housing) },initialData ,users= users, usersOfHousing = usersOfHousing, onUserRemove = onUserDelete, onUserAdd = onUserAdd)
                }

            }
        }
    }
}

@Preview(name = "HousingDialog")
@Composable
private fun PreviewHousingDialog() {
    HousingDialog(enabled = true, onDismissRequest =  {}, onSubmit = {}, users = emptyList(), usersOfHousing = emptyList(), onUserDelete = {}, onUserAdd = {})
}