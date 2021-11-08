package io.github.binishmanandhar23.verticalcalendarlibrary.model

import android.os.Parcel
import android.os.Parcelable
import org.threeten.bp.LocalDate

/**
 * An imputable representation of a day on a calendar, based on [LocalDate].
 */
class CalendarDay : Parcelable {
    /**
     * Get this day as a [LocalDate]
     *
     * @return a date with this days information
     */
    /**
     * Everything is based on this variable for [CalendarDay].
     */
    val date: LocalDate

    /**
     * @param year new instance's year
     * @param month new instance's month as defined by [java.util.Calendar]
     * @param day new instance's day of month
     */
    private constructor(year: Int, month: Int, day: Int) {
        date = LocalDate.of(year, month, day)
    }

    /**
     * @param date [LocalDate] instance
     */
    private constructor(date: LocalDate) {
        this.date = date
    }

    /**
     * Get the year
     *
     * @return the year for this day
     */
    val year: Int
        get() = date.year

    /**
     * Get the month, represented by values from [LocalDate]
     *
     * @return the month of the year as defined by [LocalDate]
     */
    val month: Int
        get() = date.monthValue

    /**
     * Get the day
     *
     * @return the day of the month for this day
     */
    val day: Int
        get() = date.dayOfMonth

    /**
     * Determine if this day is within a specified range
     *
     * @param minDate the earliest day, may be null
     * @param maxDate the latest day, may be null
     * @return true if the between (inclusive) the min and max dates.
     */
    fun isInRange(minDate: CalendarDay?, maxDate: CalendarDay?): Boolean {
        return !(minDate != null && minDate.isAfter(this)) &&
                !(maxDate != null && maxDate.isBefore(this))
    }

    /**
     * Determine if this day is before the given instance
     *
     * @param other the other day to test
     * @return true if this is before other, false if equal or after
     */
    fun isBefore(other: CalendarDay): Boolean {
        return date.isBefore(other.date)
    }

    /**
     * Determine if this day is after the given instance
     *
     * @param other the other day to test
     * @return true if this is after other, false if equal or before
     */
    fun isAfter(other: CalendarDay): Boolean {
        return date.isAfter(other.date)
    }

    override fun equals(o: Any?): Boolean {
        return o is CalendarDay && date == o.date
    }

    override fun hashCode(): Int {
        return hashCode(date.year, date.monthValue, date.dayOfMonth)
    }

    override fun toString(): String {
        return ("CalendarDay{" + date.year + "-" + date.monthValue + "-"
                + date.dayOfMonth + "}")
    }

    /*
   * Parcelable Stuff
   */
    constructor(`in`: Parcel) : this(`in`.readInt(), `in`.readInt(), `in`.readInt()) {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(date.year)
        dest.writeInt(date.monthValue)
        dest.writeInt(date.dayOfMonth)
    }

    companion object {
        /**
         * Get a new instance set to today
         *
         * @return CalendarDay set to today's date
         */
        fun today(): CalendarDay {
            return from(LocalDate.now())!!
        }

        /**
         * Get a new instance set to the specified day
         *
         * @param year new instance's year
         * @param month new instance's month as defined by [java.util.Calendar]
         * @param day new instance's day of month
         * @return CalendarDay set to the specified date
         */
        fun from(year: Int, month: Int, day: Int): CalendarDay {
            return CalendarDay(year, month, day)
        }

        /**
         * Get a new instance set to the specified day
         *
         * @param date [LocalDate] to pull date information from. Passing null will return null
         * @return CalendarDay set to the specified date
         */
        fun from(date: LocalDate?): CalendarDay? {
            return if (date == null) {
                null
            } else CalendarDay(date)
        }

        private fun hashCode(year: Int, month: Int, day: Int): Int {
            //Should produce hashes like "20150401"
            return year * 10000 + month * 100 + day
        }

        @JvmField
        val CREATOR: Parcelable.Creator<CalendarDay?> = object : Parcelable.Creator<CalendarDay?> {
            override fun createFromParcel(`in`: Parcel): CalendarDay? {
                return CalendarDay(`in`)
            }

            override fun newArray(size: Int): Array<CalendarDay?> {
                return arrayOfNulls(size)
            }
        }
    }
}