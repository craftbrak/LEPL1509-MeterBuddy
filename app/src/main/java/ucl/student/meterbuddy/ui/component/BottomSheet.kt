package ucl.student.meterbuddy.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    showBottomSheet: Boolean,
    onDismissRequest: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    if (showBottomSheet)
        ModalBottomSheet(onDismissRequest = onDismissRequest) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(25.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    onEditClick()
                }) {
                    Text("Edit")
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                }
                Button(onClick = {
                    onDeleteClick()
                }) {
                    Text("Delete")
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                }

            }
            Spacer(modifier = Modifier.heightIn(30.dp))
        }
}

@Preview(name = "BottomSheet")
@Composable
private fun PreviewBottomSheet() {
    BottomSheet(true,{},{},{})
}