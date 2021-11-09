package io.github.binishmanandhar23.verticalcalendarlibrary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jakewharton.threetenabp.AndroidThreeTen
import io.github.binishmanandhar23.verticalcalendarlibrary.model.CalendarDay
import io.github.binishmanandhar23.verticalcalendarlibrary.model.CalendarVisualModifications
import io.github.binishmanandhar23.verticalcalendarlibrary.ui.theme.VerticalCalendarTheme
import android.util.DisplayMetrics
import androidx.lifecycle.MutableLiveData
import org.threeten.bp.LocalDate


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        setContent {
            VerticalCalendarTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val displayMetrics: DisplayMetrics = resources.displayMetrics
                    MiniCalendar(displayMetrics)
                }
            }
        }
    }
}

@Composable
fun VerticalCalendar() {
    val listState = rememberLazyListState()
    val selectedDate = MutableLiveData<LocalDate>(LocalDate.now())
    val coroutineScope = rememberCoroutineScope()
    val calendarDates: Collection<CalendarDay>? by remember { mutableStateOf(null) }
    val startingMonthFromCurrentMonth = 60
    VerticalCalendarLibrary().initialize(
        cellSize = 50.dp,
        listState = listState,
        calendarDates = calendarDates,
        calendarVisualModifications = CalendarVisualModifications(),
        mutableSelectedDate = selectedDate
    ) {

    }
}

@Composable
fun MiniCalendar(displayMetrics: DisplayMetrics) {
    /*val dpHeight = displayMetrics.heightPixels / displayMetrics.density*/
    val dpWidth = displayMetrics.widthPixels / displayMetrics.density
    val selectedDate = MutableLiveData<LocalDate>(LocalDate.now())
    val calendarDates: Collection<CalendarDay>? by remember { mutableStateOf(null) }
    MiniCalendarLibrary().initialize(
        widthSize = dpWidth.dp,
        calendarDates = calendarDates,
        calendarVisualModifications = CalendarVisualModifications(),
        mutableSelectedDate = selectedDate
    ) {

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    VerticalCalendarTheme {
        VerticalCalendar()
    }
}