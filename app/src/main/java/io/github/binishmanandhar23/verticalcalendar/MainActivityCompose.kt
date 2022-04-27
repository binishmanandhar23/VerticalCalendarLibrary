package io.github.binishmanandhar23.verticalcalendar

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.jakewharton.threetenabp.AndroidThreeTen
import io.github.binishmanandhar23.verticalcalendarlibrary.model.PopulatingData
import io.github.binishmanandhar23.verticalcalendar.ui.theme.VerticalCalendarTheme
import io.github.binishmanandhar23.verticalcalendar.util.VerticalCalendarAppUtils
import io.github.binishmanandhar23.verticalcalendar.util.VerticalCalendarAppUtils.getDeviceFullHeight
import io.github.binishmanandhar23.verticalcalendar.util.VerticalCalendarAppUtils.getDummyCalendarData
import io.github.binishmanandhar23.verticalcalendarlibrary.VerticalCalendarLibrary
import org.threeten.bp.LocalDate

class MainActivityCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this) // VERY IMPORTANT
        val selectedDate = MutableLiveData(LocalDate.now())
        setContent {
            VerticalCalendarTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    VerticalCalendarLibrary().initialize(
                        listState = rememberLazyListState(),
                        mutableSelectedDate = selectedDate,
                        calendarDates = PopulatingData(
                            listOf(
                                PopulatingData.IndividualData(
                                    populatedDate = getDummyCalendarData(IntRange(1, 5)),
                                    customModifier = Modifier
                                        .size(4.dp)
                                        .background(
                                            VerticalCalendarAppUtils.getIndicatorColorBasedOnTheme(),
                                            CircleShape
                                        )
                                ),
                                PopulatingData.IndividualData(
                                    populatedDate = getDummyCalendarData(IntRange(10, 20)),
                                    customModifier = Modifier
                                        .size(4.dp)
                                        .background(
                                            VerticalCalendarAppUtils.getSecondIndicatorColorBasedOnTheme(),
                                            CircleShape
                                        )
                                )
                            )
                        ),
                        fullCalendarHeight = LocalContext.current.getDeviceFullHeight().dp - 80.dp,
                    )
                }
                Snackbar() {
                    Text(text = "${selectedDate.observeAsState().value}")
                }
            }
        }
    }
}