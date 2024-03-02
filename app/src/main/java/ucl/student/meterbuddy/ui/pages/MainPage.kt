package ucl.student.meterbuddy.ui.pages

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ucl.student.meterbuddy.data.model.entity.Meter
import ucl.student.meterbuddy.data.model.enums.MeterIcon
import ucl.student.meterbuddy.data.model.enums.Unit
import ucl.student.meterbuddy.ui.screen.MeterDetailsScreen

@Composable
fun MainPage() {
    val navigator = LocalNavigator.currentOrThrow
    Row {
        Button(onClick = { navigator.push(MeterDetailsScreen( Meter(1,"test", Unit.CUBIC_METER, MeterIcon.Water,1,1,493.291))) }) {
            Text("Go to Meter")
        }
        Button(onClick = { navigator.push(MeterDetailsScreen( Meter(1,"tes2", Unit.LITER, MeterIcon.Gas,2,1,495.291))) }) {
            Text("Go to Meter")
        }
        Button(onClick = { navigator.push(MeterDetailsScreen( Meter(1,"test34", Unit.KILO_WATT_HOUR, MeterIcon.Electricity,3,1,491.291))) }) {
            Text("Go to Meter")
        }
    }


}