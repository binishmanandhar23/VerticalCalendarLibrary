package io.github.binishmanandhar23.verticalcalendarlibrary.repository

import androidx.lifecycle.MutableLiveData
import io.github.binishmanandhar23.verticalcalendarlibrary.util.VerticalCalendarUtils.NUMBEROFMONTHS
import io.github.binishmanandhar23.verticalcalendarlibrary.util.VerticalCalendarUtils.NUMBEROFWEEKS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

class CalendarPagingRepoV2 {
    fun getDates(): MutableLiveData<ArrayList<LocalDate>>{
        val mutableData = MutableLiveData<ArrayList<LocalDate>>()
        MainScope().launch(Dispatchers.IO) {
            val listOfDates = ArrayList<LocalDate>()
            val now = LocalDate.now()
            for(i in -NUMBEROFMONTHS.toLong()..NUMBEROFMONTHS.toLong()){
                listOfDates.add(now.plusMonths(i))
            }
            mutableData.postValue(listOfDates)
        }
        return mutableData
    }

    fun getDatesForMini(): MutableLiveData<ArrayList<LocalDate>>{
        val mutableData = MutableLiveData<ArrayList<LocalDate>>()
        MainScope().launch(Dispatchers.IO) {
            val listOfDates = ArrayList<LocalDate>()
            val now = LocalDate.now()
            for(i in -NUMBEROFWEEKS.toLong()..NUMBEROFWEEKS.toLong()){
                listOfDates.add(now.plusWeeks(i))
            }
            mutableData.postValue(listOfDates)
        }
        return mutableData
    }
}