package io.github.binishmanandhar23.verticalcalendarlibrary.util

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
}