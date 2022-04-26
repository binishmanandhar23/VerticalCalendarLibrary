package io.github.binishmanandhar23.verticalcalendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import io.github.binishmanandhar23.verticalcalendarlibrary.repository.CalendarPagingRepo
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

class CalendarViewModel(private val calendarPagingRepo: CalendarPagingRepo):ViewModel() {
    fun getCalendarData(): Flow<PagingData<LocalDate>> = calendarPagingRepo.getCalendarData()
}