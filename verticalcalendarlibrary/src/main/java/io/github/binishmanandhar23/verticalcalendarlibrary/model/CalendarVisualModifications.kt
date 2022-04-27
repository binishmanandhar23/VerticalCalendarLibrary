package io.github.binishmanandhar23.verticalcalendarlibrary.model

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class CalendarVisualModifications {
    var textStyleForWeekDays: TextStyle = TextStyle()
    var textStyleForHeading: TextStyle = TextStyle()
    var textStyleForBody: TextStyle = TextStyle()
    var textStyleForSelectedDays: TextStyle = TextStyle()
    var todayBackgroundColor: Color = Color.Gray
    var textStyleForToday: TextStyle = TextStyle()

    var highlightImagePainter: Painter? = null

    var addDivider: Boolean = false
    var weekDaysPaddingForMiniCalendar: Dp = 10.dp
}