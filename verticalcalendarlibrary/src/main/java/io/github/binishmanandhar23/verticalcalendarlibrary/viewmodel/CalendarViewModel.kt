package io.github.binishmanandhar23.verticalcalendarlibrary.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import io.github.binishmanandhar23.verticalcalendarlibrary.repository.CalendarPagingRepo
import io.github.binishmanandhar23.verticalcalendarlibrary.repository.CalendarPagingRepoV2
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

class CalendarViewModel(private val calendarPagingRepo: CalendarPagingRepo, private val calendarPagingRepoV2: CalendarPagingRepoV2? = null):ViewModel() {
    fun getCalendarData(): Flow<PagingData<LocalDate>> = calendarPagingRepo.getCalendarData()

    fun getCalendarDataV2(): MutableLiveData<ArrayList<LocalDate>>? = calendarPagingRepoV2?.getDates()

    fun getCalendarDataV2ForMini(): MutableLiveData<ArrayList<LocalDate>>? = calendarPagingRepoV2?.getDatesForMini()
}