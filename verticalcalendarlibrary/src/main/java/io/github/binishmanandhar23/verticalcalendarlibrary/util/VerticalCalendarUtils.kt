package io.github.binishmanandhar23.verticalcalendarlibrary.util

import android.content.Context
import io.github.binishmanandhar23.verticalcalendarlibrary.model.CalendarDay
import io.github.binishmanandhar23.verticalcalendarlibrary.model.PopulatingData
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.Period
import org.threeten.bp.temporal.ChronoUnit

object VerticalCalendarUtils {
    const val NUMBEROFWEEKS = 265
    const val NUMBEROFMONTHS = 60

    val weekDayOffset: MutableMap<Int, Int> =
        hashMapOf(
            1 to 0,
            2 to 1,
            3 to 2,
            4 to 3,
            5 to 4,
            6 to 5,
            7 to 6)

    fun getMonthIndex(selectedDate: LocalDate): Int{
        val todayDate = LocalDate.now().withDayOfMonth(1)
        val difference = Period.between(todayDate, selectedDate.withDayOfMonth(1))
        return (NUMBEROFMONTHS + ((12 * difference.years) + difference.months))
    }

    fun getWeekIndex(selectedDate: LocalDate): Int{
        val todayDate = LocalDate.now()
        val difference = ChronoUnit.WEEKS.between(todayDate.with(DayOfWeek.MONDAY), selectedDate.with(DayOfWeek.MONDAY))
        return (NUMBEROFWEEKS + difference).toInt()
    }

    fun Context.getDeviceFullWidth(): Int = this.resources.displayMetrics.let { (it.widthPixels / it.density).toInt() }
    fun Context.getDeviceFullHeight(): Int = this.resources.displayMetrics.let { (it.heightPixels / it.density).toInt() }

    fun isPopulated(calendarDates: List<PopulatingData.IndividualData>?, currentDate: LocalDate?): Boolean{
        calendarDates?.forEach {
            it.populatedDate.forEach { calendarDay ->
                if(calendarDay.date == currentDate)
                    return true
            }
        }
        return false
    }

    fun isHighlighted(calendarDates: Collection<CalendarDay>?, currentDate: LocalDate?): Boolean {
        calendarDates?.forEach {
            if (it.date == currentDate)
                return true
        }
        return false
    }
}