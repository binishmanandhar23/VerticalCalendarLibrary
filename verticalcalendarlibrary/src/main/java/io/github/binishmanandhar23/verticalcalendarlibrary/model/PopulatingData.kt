package io.github.binishmanandhar23.verticalcalendarlibrary.model

import androidx.compose.ui.Modifier

data class PopulatingData(val collectionOfDates: List<IndividualData>? = null, val highlightedDates: Collection<CalendarDay>? = null){
    data class IndividualData(val populatedDate: Collection<CalendarDay> = emptyList(), val customModifier: Modifier = Modifier)
}