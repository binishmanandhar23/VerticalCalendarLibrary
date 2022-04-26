package io.github.binishmanandhar23.verticalcalendarlibrary.model

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

class CalendarVisualModifications {
    var textStyleForWeekDays: TextStyle = TextStyle()
    var textStyleForHeading: TextStyle = TextStyle()
    var textStyleForBody: TextStyle = TextStyle()
    var textStyleForSelectedDays: TextStyle = TextStyle()
    var todayBackgroundColor: Color = Color.Gray
    var textStyleForToday: TextStyle = TextStyle()

    var highlightImageId: Int? = null


    var medicationIndicator: Modifier = Modifier
    var appointmentIndicator: Modifier = Modifier
    var otherIndicator: Modifier = Modifier
}