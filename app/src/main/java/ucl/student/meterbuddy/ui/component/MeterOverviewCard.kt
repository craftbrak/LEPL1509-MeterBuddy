package ucl.student.meterbuddy.ui.component


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ucl.student.meterbuddy.R
import ucl.student.meterbuddy.data.model.enums.MeterIcon
import ucl.student.meterbuddy.data.model.enums.MeterUnit


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MeterOverviewCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    meterName: String,
    meterIcon: MeterIcon,
    lastReading: String?,
    readingUnit: String,
    trendIcon: String,
    trendValue: Float,
    monthlyCost: Float,
    currencySymbol: String
) {
    ElevatedCard(modifier = modifier, onClick = onClick) {
        Column(modifier= Modifier
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),verticalArrangement = Arrangement.SpaceAround) {
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {

                Icon(imageVector = ImageVector.vectorResource(meterIcon.icon), contentDescription = "Meter Icon")
                Spacer(Modifier.width(8.dp))
                Text(text = meterName)
                Spacer(Modifier.weight(1f))
                Text(text = lastReading ?: stringResource(R.string.no_reading))
                Spacer(Modifier.width(8.dp))
                Text(text = if (lastReading != null) readingUnit else "")
            }
            Spacer(Modifier.height(20.dp))
            if (lastReading !=null){
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically) {
                    Spacer(Modifier.width(10.dp))
                    Text(text = monthlyCost.toString())
                    Spacer(Modifier.width(8.dp))
                    Text(text = currencySymbol)
                    Spacer(Modifier.weight(1f))
                    ElevatedSuggestionChip(modifier = Modifier.clickable(enabled = false) {}, onClick = { /*TODO*/ },label = {
                        Text(text = trendValue.toString())
                    })
                    Spacer(Modifier.width(10.dp))
                }
            }

        }
    }
}

@Preview(name = "MeterOverviewCard")
@Composable
fun MeterOverviewCardPreview() {
    MeterOverviewCard(
        meterName = "My Electricity Meter",
        meterIcon = MeterIcon.Electricity,
        lastReading = 192384.0f.toString(),
        readingUnit = MeterUnit.CUBIC_METER.unit,
        trendIcon = "up",
        trendValue = .23f,
        monthlyCost = 213f,
        currencySymbol = "$"
    )
}