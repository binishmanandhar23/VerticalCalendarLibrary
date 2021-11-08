package io.github.binishmanandhar23.verticalcalendarlibrary.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

class CalendarPagingRepo(private val startingMonthFromCurrentMonth: Int) {
    private val calendarPagingSource = CalendarPagingSource().apply { changeStartingMonth(startingMonthFromCurrentMonth) }
    fun getCalendarData(): Flow<PagingData<LocalDate>> = Pager(config = PagingConfig(
        PAGING_SIZE, enablePlaceholders = true, initialLoadSize = PAGING_SIZE, prefetchDistance = 1), pagingSourceFactory = {
            calendarPagingSource
    }).flow

    companion object{
        const val PAGING_SIZE = 12
    }
}