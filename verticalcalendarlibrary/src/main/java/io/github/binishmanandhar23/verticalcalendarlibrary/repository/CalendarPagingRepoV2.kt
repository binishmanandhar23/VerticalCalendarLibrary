package io.github.binishmanandhar23.verticalcalendarlibrary.repository

import androidx.lifecycle.MutableLiveData
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
            for(i in -60L..60L){
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
            for(i in -60L..60L){
                listOfDates.add(now.plusWeeks(i))
            }
            mutableData.postValue(listOfDates)
        }
        return mutableData
    }
}