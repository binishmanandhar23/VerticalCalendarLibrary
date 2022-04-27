package io.github.binishmanandhar23.verticalcalendar.util

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.binishmanandhar23.verticalcalendar.ui.theme.IndicatorDarkColor
import io.github.binishmanandhar23.verticalcalendar.ui.theme.IndicatorLightColor
import io.github.binishmanandhar23.verticalcalendar.ui.theme.IndicatorSecondDarkColor
import io.github.binishmanandhar23.verticalcalendar.ui.theme.IndicatorSecondLightColor
import io.github.binishmanandhar23.verticalcalendarlibrary.model.CalendarDay
import org.threeten.bp.LocalDate
import kotlin.random.Random

object VerticalCalendarAppUtils {
    fun Context.getDeviceFullHeight(): Int = this.resources.displayMetrics.let { (it.heightPixels / it.density).toInt() }
    fun Context.getDeviceFullWidth(): Int = this.resources.displayMetrics.let { (it.widthPixels / it.density).toInt() }

    @Composable
    fun getIndicatorColorBasedOnTheme(): Color = if(isSystemInDarkTheme()) IndicatorLightColor else IndicatorDarkColor

    @Composable
    fun getSecondIndicatorColorBasedOnTheme(): Color = if(isSystemInDarkTheme()) IndicatorSecondLightColor else IndicatorSecondDarkColor

    fun getDummyCalendarData(randomIntRange: IntRange = IntRange(Random.nextInt(10), Random.nextInt(20))): ArrayList<CalendarDay> =
        ArrayList<CalendarDay>().apply {
            for(i in randomIntRange){
                CalendarDay.from(LocalDate.now().plusDays(i.toLong()))?.let {
                    add(it)
                }
            }
        }
}