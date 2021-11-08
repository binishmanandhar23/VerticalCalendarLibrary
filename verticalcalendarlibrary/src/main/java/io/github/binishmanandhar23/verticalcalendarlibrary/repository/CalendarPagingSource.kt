package io.github.binishmanandhar23.verticalcalendarlibrary.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.threeten.bp.LocalDate


class CalendarPagingSource: PagingSource<Int, LocalDate>() {
    private var startingMonthFromCurrentMonth: Int = 60

    fun changeStartingMonth(startingMonthFromCurrentMonth: Int){
        this.startingMonthFromCurrentMonth = startingMonthFromCurrentMonth
    }

    override fun getRefreshKey(state: PagingState<Int, LocalDate>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LocalDate> {
        val position = params.key ?: 0
        val calendarDateModelList = ArrayList<LocalDate>()
        val now = LocalDate.now().plusMonths(position.toLong())
        calendarDateModelList.add(now)
        return LoadResult.Page(
            data = calendarDateModelList,
            prevKey = if(position == (startingMonthFromCurrentMonth - (startingMonthFromCurrentMonth * 2))) null else position - 1,
            nextKey = position + 1
        )
    }
}