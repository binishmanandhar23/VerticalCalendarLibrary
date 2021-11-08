package io.github.binishmanandhar23.verticalcalendarlibrary.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.github.binishmanandhar23.verticalcalendarlibrary.repository.CalendarPagingSource
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

class CalendarPagingRepo(private val startingMonthFromCurrentMonth: Int) {
    fun getCalendarData(): Flow<PagingData<LocalDate>> = Pager(config = PagingConfig(
        PAGING_SIZE, enablePlaceholders = true, initialLoadSize = PAGING_SIZE), pagingSourceFactory = {
            CalendarPagingSource().apply { changeStartingMonth(startingMonthFromCurrentMonth) }
    }).flow

    companion object{
        const val PAGING_SIZE = 12
    }
}