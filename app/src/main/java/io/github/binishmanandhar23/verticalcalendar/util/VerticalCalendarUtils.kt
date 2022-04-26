package io.github.binishmanandhar23.verticalcalendar.util

import android.content.Context

object VerticalCalendarUtils {
    fun Context.getDeviceFullHeight(): Float = this.resources.displayMetrics.let { it.heightPixels * it.density }
    fun Context.getDeviceFullWidth(): Float = this.resources.displayMetrics.let { it.widthPixels * it.density }
}