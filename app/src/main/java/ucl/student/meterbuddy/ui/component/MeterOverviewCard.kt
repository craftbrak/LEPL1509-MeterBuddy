package ucl.student.meterbuddy.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.MutatePriority
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ucl.student.meterbuddy.R
import ucl.student.meterbuddy.data.model.enums.MeterIcon
import ucl.student.meterbuddy.data.model.enums.MeterUnit


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MeterOverviewCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    meterName: String,
    meterIcon: MeterIcon,
    lastReading: String?,
    readingUnit: String,
    trendIcon: Int,
    trendValue: Float,
    monthlyCost: Double,
    monthlyCostIcon: Int,
    currencySymbol: String,
    consumption: Boolean
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
                Spacer(Modifier.width(4.dp))
                Spacer(Modifier.weight(1f))
                Text(text = lastReading ?: stringResource(R.string.no_reading))
                Spacer(Modifier.width(8.dp))
                Text(text = if (lastReading != null) readingUnit else "")
            }
            Spacer(Modifier.height(20.dp))
            if (lastReading !=null){
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically) {
                    Image( painter = painterResource(id = monthlyCostIcon), contentDescription = "Trend Icon", colorFilter = ColorFilter.tint(Color.DarkGray))
                    Spacer(Modifier.width(3.dp))
                    val formattedmonthlyCostValue = String.format("%.2f", monthlyCost)
                    Text(text = formattedmonthlyCostValue)
                    Spacer(Modifier.width(4.dp))
                    Text(text = currencySymbol)
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    val tooltipState = remember { mutableStateOf(TooltipState()) }
                    val scope = rememberCoroutineScope()
                    TooltipBox(positionProvider =TooltipDefaults.rememberPlainTooltipPositionProvider() , tooltip = {
                        PlainTooltip{
                            if (consumption) {
                                Text(text = "Estimated cost for your recent consumption (last two readings)")
                            } else {
                                Text(text = "Estimated cost of your recent production (last two readings)")
                            }
                        }
                    }, state = tooltipState.value , content = {
                        IconButton(onClick = { scope.launch { tooltipState.value.show(MutatePriority.Default) } } ,) {
                            Icon(
                                imageVector = Icons.Filled.Help,
                                contentDescription = Icons.Filled.Help.name
                            )
                        }

                    })
                    Spacer(Modifier.weight(1f))
                    Image( painter = painterResource(id = trendIcon), contentDescription = "Trend Icon")
                    ElevatedSuggestionChip(modifier = Modifier.clickable(enabled = false) {}, onClick = { /*TODO*/ },label = {
                        val formattedTrendValue = String.format("%.2f", trendValue)
                        Text(text = "$formattedTrendValue %")
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
        trendIcon = R.drawable.trend_up_red_icon,
        trendValue = 23.303904f,
        monthlyCost = 213.5,
        monthlyCostIcon = R.drawable.additive_icon,
        currencySymbol = "$",
        consumption = true
    )
}