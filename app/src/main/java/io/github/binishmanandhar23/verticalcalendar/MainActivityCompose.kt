package io.github.binishmanandhar23.verticalcalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.jakewharton.threetenabp.AndroidThreeTen
import io.github.binishmanandhar23.verticalcalendarlibrary.model.PopulatingData
import io.github.binishmanandhar23.verticalcalendar.ui.theme.VerticalCalendarTheme
import io.github.binishmanandhar23.verticalcalendar.util.VerticalCalendarUtils.getDeviceFullHeight
import io.github.binishmanandhar23.verticalcalendarlibrary.VerticalCalendarLibrary
import org.threeten.bp.LocalDate

class MainActivityCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this) // VERY IMPORTANT
        setContent {
            VerticalCalendarTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val selectedDate = MutableLiveData(LocalDate.now())
                    VerticalCalendarLibrary().initialize(
                        listState = rememberLazyListState(),
                        coroutineScope = rememberCoroutineScope(),
                        mutableSelectedDate = selectedDate,
                        calendarDates = PopulatingData(),
                        fullCalendarHeight = LocalContext.current.getDeviceFullHeight().dp - 50.dp,
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    VerticalCalendarTheme {
        Greeting("Android")
    }
}