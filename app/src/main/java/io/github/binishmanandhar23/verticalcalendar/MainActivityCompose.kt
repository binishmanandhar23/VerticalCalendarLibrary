package io.github.binishmanandhar23.verticalcalendar

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.jakewharton.threetenabp.AndroidThreeTen
import io.github.binishmanandhar23.verticalcalendar.enums.Mode
import io.github.binishmanandhar23.verticalcalendar.ui.theme.TextColorForDarkTheme
import io.github.binishmanandhar23.verticalcalendar.ui.theme.TextColorForLightTheme
import io.github.binishmanandhar23.verticalcalendarlibrary.model.PopulatingData
import io.github.binishmanandhar23.verticalcalendar.ui.theme.VerticalCalendarTheme
import io.github.binishmanandhar23.verticalcalendar.util.VerticalCalendarAppUtils
import io.github.binishmanandhar23.verticalcalendar.util.VerticalCalendarAppUtils.getDeviceFullHeight
import io.github.binishmanandhar23.verticalcalendar.util.VerticalCalendarAppUtils.getDummyCalendarData
import io.github.binishmanandhar23.verticalcalendarlibrary.VerticalCalendarLibrary
import io.github.binishmanandhar23.verticalcalendarlibrary.enums.CalendarType
import io.github.binishmanandhar23.verticalcalendarlibrary.model.CalendarVisualModifications
import org.threeten.bp.LocalDate

class MainActivityCompose : ComponentActivity() {
    private val selectedDate by lazy { MutableLiveData(LocalDate.now()) }

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this) // VERY IMPORTANT
        setContent {
            val hapticFeedback = LocalHapticFeedback.current
            val snackBarHostState = remember { SnackbarHostState() }
            val selectedDateState = selectedDate.observeAsState().value
            var mode by remember { mutableStateOf(Mode.SINGLE) }
            val modeButton by animateColorAsState(targetValue = if (mode == Mode.SINGLE) Color.Magenta else Color.Red)
            VerticalCalendarTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AnimatedContent(targetState = mode, transitionSpec = {
                            slideIntoContainer(
                                towards = if(this.initialState == Mode.SINGLE) AnimatedContentScope.SlideDirection.End else AnimatedContentScope.SlideDirection.Start,
                                animationSpec = tween()
                            ) with slideOutOfContainer(
                                towards = if(this.initialState == Mode.MULTI) AnimatedContentScope.SlideDirection.End else AnimatedContentScope.SlideDirection.Start,
                                animationSpec = tween()
                            )
                        }) { mode ->
                            if (mode == Mode.SINGLE)
                                SingleMode()
                            else
                                MultiMode()
                        }

                        FloatingActionButton(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                mode = if (mode == Mode.SINGLE) Mode.MULTI else Mode.SINGLE
                            },
                            shape = CircleShape,
                            modifier = Modifier
                                .padding(bottom = 20.dp, end = 20.dp)
                                .align(Alignment.BottomEnd),
                            backgroundColor = modeButton
                        ) {
                            Image(
                                painter = painterResource(id = if (mode == Mode.SINGLE) R.drawable.ic_switch_to_mini else R.drawable.ic_switch_to_full),
                                contentDescription = if (mode == Mode.SINGLE) "Switch to multi mode" else "Switch to single mode",
                                colorFilter = ColorFilter.tint(color = Color.White)
                            )
                        }

                        SnackbarHost(
                            hostState = snackBarHostState,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 20.dp)
                        ) {
                            Text(
                                it.message,
                                modifier = Modifier
                                    .background(
                                        color = VerticalCalendarAppUtils.getIndicatorColorBasedOnTheme(),
                                        shape = RoundedCornerShape(40.dp)
                                    )
                                    .padding(vertical = 10.dp, horizontal = 40.dp),
                                style = TextStyle(color = if (isSystemInDarkTheme()) TextColorForDarkTheme else TextColorForLightTheme)
                            )
                        }
                        LaunchedEffect(key1 = selectedDateState) {
                            snackBarHostState.showSnackbar(
                                "$selectedDateState",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                }

            }
        }
    }

    @Composable
    fun SingleMode() {
        val hapticFeedback = LocalHapticFeedback.current
        var calendarType by remember { mutableStateOf(CalendarType.FULL) }
        val switchButton by animateColorAsState(targetValue = if (calendarType == CalendarType.FULL) Color.Cyan else Color.Green)

        Box(modifier = Modifier.fillMaxSize()) {
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
                fullCalendarHeight = LocalContext.current.getDeviceFullHeight().dp,
                calendarTypeState = calendarType,
                calendarVisualModifications = CalendarVisualModifications().apply {
                    textStyleForWeekDays =
                        TextStyle(
                            color = if (isSystemInDarkTheme()) Color.Yellow else Color.DarkGray,
                            fontSize = 10.sp
                        )
                }
            )
            FloatingActionButton(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    calendarType =
                        if (calendarType == CalendarType.FULL) CalendarType.MINI else CalendarType.FULL
                },
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 20.dp, start = 20.dp)
                    .align(Alignment.BottomStart),
                backgroundColor = switchButton
            ) {
                Image(
                    painter = painterResource(id = if (calendarType == CalendarType.FULL) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down),
                    contentDescription = if (calendarType == CalendarType.FULL) "Switch to mini mode" else "Switch to full mode",
                    colorFilter = ColorFilter.tint(color = Color.Black)
                )
            }
        }
    }

    @Composable
    fun MultiMode() {
        val clickInitiatedFrom =
            MutableLiveData(CalendarType.FULL) // IMPORTANT for communication between to CalendarModes
        val calendarVisualModifications = CalendarVisualModifications().apply {
            textStyleForWeekDays =
                TextStyle(
                    color = if (isSystemInDarkTheme()) Color.Yellow else Color.DarkGray,
                    fontSize = 10.sp
                )
        }
        val populatingData = PopulatingData(
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
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Mini Mode", modifier = Modifier
                    .background(color = Color.Cyan, shape = RoundedCornerShape(20.dp))
                    .padding(vertical = 5.dp, horizontal = 20.dp), color = Color.Black
            )
            VerticalCalendarLibrary().initialize(
                listState = rememberLazyListState(),
                mutableSelectedDate = selectedDate,
                calendarDates = populatingData,
                calendarTypeState = CalendarType.MINI,
                calendarVisualModifications = calendarVisualModifications,
                onClickInitiatedFrom = clickInitiatedFrom // IMPORTANT for communication between to CalendarModes
            )
            Text(
                text = "Full Mode", modifier = Modifier
                    .padding(top = 5.dp)
                    .background(color = Color.Cyan, shape = RoundedCornerShape(20.dp))
                    .padding(vertical = 5.dp, horizontal = 20.dp), color = Color.Black
            )
            VerticalCalendarLibrary().initialize(
                listState = rememberLazyListState(),
                mutableSelectedDate = selectedDate,
                calendarDates = populatingData,
                fullCalendarHeight = LocalContext.current.getDeviceFullHeight().dp - 145.dp,
                calendarTypeState = CalendarType.FULL,
                calendarVisualModifications = calendarVisualModifications,
                onClickInitiatedFrom = clickInitiatedFrom // IMPORTANT for communication between to CalendarModes
            )
        }
    }
}