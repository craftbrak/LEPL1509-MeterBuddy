package ucl.student.meterbuddy.ui.component

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ucl.student.meterbuddy.data.model.enums.MeterIcon
import ucl.student.meterbuddy.ui.theme.MeterBuddyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeterOverviewCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    meterName: String,
    meterIcon: MeterIcon,
    lastReading: Float,
    readingUnit: String,
    tendanceIcon: String,
    tendenceValue: Float,
    monthlyCost: Float,
    currencySymbol: String
) {
    ElevatedCard(modifier = modifier, onClick = onClick) {
        Column(modifier= Modifier
            .fillMaxWidth(),verticalArrangement = Arrangement.SpaceAround) {
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround) {
                Row(
                    Modifier
                        .padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround){
// TODO                    Icon(painter = meterIcon.icon, contentDescription = "Meter Icon")
                    Text(text = meterName)
                }
                Row(
                    Modifier
                        .padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly){
                    Text(text = lastReading.toString())
                    Text(text = readingUnit)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Row(
                    Modifier
                        .padding(20.dp)
                        .fillMaxWidth(.5F), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
                    Text(text = monthlyCost.toString())
                    Text(text = currencySymbol)
                }
                Row(
                    Modifier
                        .padding(20.dp)
                        .fillMaxWidth(.5F), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
                    ElevatedSuggestionChip(onClick = { /*TODO*/ },label={
                        Text(text = tendenceValue.toString())
                    })
                }
            }
        }
    }
}

@Preview(name = "MeterOverviewCard")
@Composable
fun PreviewMeterOverviewCard() {
    MeterBuddyTheme {
        MeterOverviewCard(Modifier.padding(9.dp), meterName = "Electricity", meterIcon = MeterIcon.Water , currencySymbol = "eur", lastReading = 182.159F, monthlyCost = 291F, readingUnit = "Kwh", tendanceIcon = "", tendenceValue = 29F)
    }
}